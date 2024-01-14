package me.hamtom.thor.directory.domain.create;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.hamtom.thor.directory.domain.common.DirectoryService;
import me.hamtom.thor.directory.domain.create.dto.CreateCommand;
import me.hamtom.thor.directory.domain.create.dto.CreateResult;
import me.hamtom.thor.directory.domain.common.dto.ParentDirectoriesInfoDto;
import me.hamtom.thor.directory.domain.common.exception.PredictableRuntimeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CreateService {
    @Value("${config.policy.create-parents.limit}")
    private int createParentsLimit;
    @Value("${config.capacity}")
    private int totalCapacity;

    private final DirectoryService directoryService;

    public CreateResult createDirectory(CreateCommand command) {
        //create command 가져오기
        String pathName = command.getPathName();
        String owner = command.getOwner();
        String group = command.getGroup();
        String permissions = command.getPermissions();
        int size = command.getSize();
        boolean isCreateMissingParent = command.isCreateMissingParent();
        boolean isFlexibleCapacity = command.isFlexibleCapacity();

        //루트 패스 확인
        ifRootPath(pathName);

        //Directory 중복 확인. 중복 -> 실패 응답
        directoryService.checkAvailablePathName(pathName);

        //부모 디렉토리 확인
        ParentDirectoriesInfoDto parentDirectoriesInfoDto = directoryService.getParentDirectoriesInfo(pathName);

        //부모 디렉터리 누락일 경우 옵션값 확인
        boolean isCreateWithParent = ifMissingParentExist(isCreateMissingParent, parentDirectoriesInfoDto);

        //디렉토리 남은 용량 확인, 없을 경우 실패, 부족할 경우 옵션값 확인
        int toCreateNum = parentDirectoriesInfoDto.countMissingParent()+1;
        size = checkAvailableCapacityForSize(size, isFlexibleCapacity, toCreateNum);

        //부모 디렉토리 create
        List<String> createdParentDirectories = new ArrayList<>();
        if (isCreateWithParent) {
            List<String> missingDirectories = parentDirectoriesInfoDto.getMissingDirectories();
            List<String> saveDirectoriesPathName = directoryService.saveDirectories(missingDirectories, owner, group, permissions, size);
            createdParentDirectories = saveDirectoriesPathName;
            log.info("부모 디렉토리 생성. pathName: {}", saveDirectoriesPathName);
        }

        //디렉토리 create
        String saveDirectoryPathName = directoryService.saveDirectory(pathName, owner, group, permissions, size);
        log.info("디렉토리 생성. pathName: {}", saveDirectoryPathName);

        return new CreateResult(saveDirectoryPathName, createdParentDirectories, size);
    }

    private void ifRootPath(String pathName) {
        boolean isRootPath = directoryService.isRootPath(pathName);
        if (isRootPath) {
            throw new PredictableRuntimeException("root 경로(/)입니다. 생성할 수 없습니다.");
        }
    }


    private boolean ifMissingParentExist(boolean isCreateMissingParent, ParentDirectoriesInfoDto parentDirectoriesInfoDto) {
        boolean isCreateWithParent = false;
        if (parentDirectoriesInfoDto.hasMissingParent()) {
            List<String> missingDirectories = parentDirectoriesInfoDto.getMissingDirectories();
            log.info("부모 디렉토리 없음. missingDirectories:{}", missingDirectories);

            //옵션값 FALSE -> 실패 응답
            if (!isCreateMissingParent) {
                throw new PredictableRuntimeException("부모 디렉토리가 없습니다. 부모 디렉토리와 함께 생성하길 원하실 경우 'createMissingParent=true' 옵션을 쿼리스트링으로 요청해주십시오.");
            }

            //생성해야 할 부모 디렉토리가 일정 갯수(config.yml) 초과시 실패 응답
            if (missingDirectories.size() >= createParentsLimit) {
                throw new PredictableRuntimeException(createParentsLimit+"개를 초과하는 부모 디렉토리가 생성이 필요합니다. 부모 디렉토리를 먼저 생성해주십시오.");
            }
            isCreateWithParent = true;
        }
        return isCreateWithParent;
    }

    private int checkAvailableCapacityForSize(int size, boolean isFlexibleCapacity, int toCreateNum) {
        int usedCapacity = directoryService.getUsedCapacity();
        int availableCapacity = totalCapacity - usedCapacity;

        //남은 용량 없는 경우
        if (availableCapacity <= 0) {
            log.info("가용 용량 없음. totalCapacity: {}, usedCapacity: {}, availableCapacity: {}", totalCapacity, usedCapacity, availableCapacity);
            throw new PredictableRuntimeException("가용 용량이 없습니다.");
        }

        //남은 용량 충분한지 확인
        //남은 용량 부족
        if (availableCapacity < (toCreateNum * size)) {
            //flexibleCapacity 확인. FALSE -> 실패 응답
            if (!isFlexibleCapacity) {
                log.info("가용 용량 부족. availableCapacity: {}, toCreateNum: {}, size {}", availableCapacity, toCreateNum, size);
                throw new PredictableRuntimeException("가용 용량이 부족합니다. 용량을 확보하거나, 'flexibleCapacity=true' 옵션을 쿼리스트링으로 요청해주십시오.");
            }

            //flexibleCapacity 확인. TRUE
            //남은 용량을 생성할 디렉토리 수에 맞게 나누기
            int allocatedCapacity = availableCapacity / toCreateNum;

            //나눈 용량이 1이 되지 않다면 실패 응답
            if (allocatedCapacity < 1) {
                log.info("가용 용량 부족. availableCapacity: {}, toCreateNum: {}, allocatedCapacity {}", availableCapacity, toCreateNum, allocatedCapacity);
                String msg = String.format("가용 용량이 부족합니다. 용량을 확보하십시오. 가용 용량: %d, 생성 디렉토리 수: %d", availableCapacity, toCreateNum);
                throw new PredictableRuntimeException(msg);
            } else {
                //나눈 용량이 1이상이라면 용량 할당
                log.info("flexibleCapacity 옵션. size {} -> {}", size, allocatedCapacity);
                size = allocatedCapacity;
            }
        }

        return size;
    }

}
