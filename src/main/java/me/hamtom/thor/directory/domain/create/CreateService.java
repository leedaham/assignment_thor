package me.hamtom.thor.directory.domain.create;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.hamtom.thor.directory.domain.common.DirectoryService;
import me.hamtom.thor.directory.domain.create.dto.CreateDirectoryDto;
import me.hamtom.thor.directory.domain.create.dto.CreateDirectoryResultDto;
import me.hamtom.thor.directory.domain.common.directory.dto.PathDetailDto;
import me.hamtom.thor.directory.domain.common.directory.dto.ParentDirectoriesInfoDto;
import me.hamtom.thor.directory.domain.common.enumerated.OptionValue;
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
    @Value("${config.request.create-parents.limit}")
    private int createParentsLimit;
    @Value("${config.capacity}")
    private int totalCapacity;

    private final DirectoryService directoryService;

    public CreateDirectoryResultDto createDirectory(CreateDirectoryDto createDirectoryDto) {
        //create 정보 가져오기
        String pathName = createDirectoryDto.getPathName();
        String owner = createDirectoryDto.getOwner();
        String group = createDirectoryDto.getGroup();
        String permissions = createDirectoryDto.getPermissions();
        int size = createDirectoryDto.getSize();
        OptionValue createMissingParent = createDirectoryDto.getCreateMissingParent();
        OptionValue flexibleCapacity = createDirectoryDto.getFlexibleCapacity();

        //Directory 중복 확인
        checkDuplicatePath(pathName);

        //상위 디렉토리 확인 - Directory path 정보 만들기
        PathDetailDto pathDetailDto = directoryService.getPathDetail(pathName);

        //상위 디렉토리 누락 정보 확인
        List<String> layers = pathDetailDto.getLayers();
        ParentDirectoriesInfoDto parentDirectoriesInfoDto = directoryService.getParentDirectoriesInfo(pathName, layers);

        //부모 디렉터리 누락 -> 부모 디렉토리 생성 옵션값 확인 후 생성
        List<String> missingDirectories = parentDirectoriesInfoDto.getMissingDirectories();
        ifMissingParentExist(createMissingParent, missingDirectories);

        //디렉토리 남은 용량 확인
        size = checkAvailableCapacityForSize(size, flexibleCapacity, missingDirectories);

        //부모 디렉토리 create
        List<String> createdParentDirectories = new ArrayList<>();
        if (!missingDirectories.isEmpty()) {
            List<String> saveDirectoriesPathName = directoryService.saveDirectories(missingDirectories, owner, group, permissions, size);
            createdParentDirectories = saveDirectoriesPathName;
            log.info("부모 디렉토리 생성. pathName: {}", saveDirectoriesPathName);
        }


        //디렉토리 create
        String saveDirectoryPathName = directoryService.saveDirectory(pathName, owner, group, permissions, size);
        log.info("디렉토리 생성. pathName: {}", saveDirectoryPathName);

        return new CreateDirectoryResultDto(saveDirectoryPathName, createdParentDirectories, size);
    }

    private void checkDuplicatePath(String pathName) {
        boolean directoryExist = directoryService.isDirectoryExist(pathName);
        //Directory 중복 -> 실패 응답
        if(directoryExist){
            throw new PredictableRuntimeException("이미 존재하는 디렉토리 입니다.");
        }
        log.info("중복 없음. pathName: {}", pathName);
    }

    private void ifMissingParentExist(OptionValue createMissingParent, List<String> missingDirectories) {
        if (!missingDirectories.isEmpty()) {
            log.info("부모 디렉토리 없음. missingDirectories:{}", missingDirectories);

            //옵션값 FALSE -> 실패 응답
            if (createMissingParent.equals(OptionValue.FALSE)) {
                throw new PredictableRuntimeException("부모 디렉토리가 없습니다. 부모 디렉토리와 함께 생성하길 원하실 경우 'createMissingParent=T' 옵션을 쿼리스트링으로 요청해주십시오.");
            }

            //생성해야 할 부모 디렉토리가 일정 갯수(config.yml) 초과시 실패 응답
            if (missingDirectories.size() >= createParentsLimit) {
                throw new PredictableRuntimeException(createParentsLimit+"개를 초과하는 부모 디렉토리가 생성이 필요합니다. 부모 디렉토리를 먼저 생성해주십시오.");
            }
        }
    }

    private int checkAvailableCapacityForSize(int size, OptionValue flexibleCapacity, List<String> missingDirectories) {
        int usedCapacity = directoryService.getUsedCapacity();
        int availableCapacity = totalCapacity - usedCapacity;
        if (availableCapacity <= 0) {
            log.info("가용 용량 없음. totalCapacity: {}, usedCapacity: {}, availableCapacity: {}", totalCapacity, usedCapacity, availableCapacity);
            throw new PredictableRuntimeException("가용 용량이 없습니다.");
        }

        //생성할 용량과 남은 용량 확인
        int numDirectoriesToCreate = missingDirectories.size() + 1;

        //남은 용량 부족시
        if (availableCapacity < (numDirectoriesToCreate * size)) {

            //flexibleCapacity 확인. FALSE -> 실패 응답
            if (flexibleCapacity.equals(OptionValue.FALSE)) {
                log.info("가용 용량 부족. availableCapacity: {}, numDirectoriesToCreate: {}, size {}", availableCapacity, numDirectoriesToCreate, size);
                throw new PredictableRuntimeException("가용 용량이 부족합니다. 용량을 확보하거나, 'flexibleCapacity=T' 옵션을 쿼리스트링으로 요청해주십시오.");
            }

            //flexibleCapacity 확인. TRUE
            //남은 용량을 생성할 디렉토리 수에 맞게 나누기
            int allocatedCapacity = availableCapacity / numDirectoriesToCreate;

            //나눈 용량이 1이 되지 않다면 실패 응답
            if (allocatedCapacity < 1) {
                log.info("가용 용량 부족. availableCapacity: {}, numDirectoriesToCreate: {}, allocatedCapacity {}", availableCapacity, numDirectoriesToCreate, allocatedCapacity);
                String msg = String.format("가용 용량이 부족합니다. 용량을 확보하십시오. 가용 용량: %d, 생성 디렉토리 수: %d", availableCapacity, numDirectoriesToCreate);
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
