package me.hamtom.thor.directory.domain.remove;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.hamtom.thor.directory.domain.common.DirectoryService;
import me.hamtom.thor.directory.domain.common.dto.ChildDirectoriesInfoDto;
import me.hamtom.thor.directory.domain.common.exception.PredictableRuntimeException;
import me.hamtom.thor.directory.domain.remove.dto.RemoveCommand;
import me.hamtom.thor.directory.domain.remove.dto.RemoveResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RemoveService {

    private final DirectoryService directoryService;

    /**
     * 디렉토리 삭제 메서드
     * @return 디렉토리 삭제 결과
     */

    public RemoveResult removeDirectory(RemoveCommand command) {
        String pathName = command.getPathName();
        boolean isRemoveWithChild = command.isRemoveWithChild();

        //경로 / 확인
        directoryService.throwExceptionIfRootPath(pathName);

        //디렉토리 경로 존재 확인. 존재 X -> 실패 응답
        directoryService.checkExistPathName(pathName);

        //자식 디렉토리 확인
        ChildDirectoriesInfoDto childDirectoriesInfo = directoryService.getChildDirectoriesInfo(pathName);
        List<String> childDirectories = childDirectoriesInfo.getChildDirectories();

        //자식과 함께 삭제 확인
        boolean isDeleteWithChild = checkDeleteWithChild(isRemoveWithChild, childDirectories);

        log.info("디렉토리 삭제. pathName: {}", pathName);

        //디렉토리 지우기
        if (isDeleteWithChild) {
            directoryService.deleteDirectoryWithChild(pathName);
        } else {
            directoryService.deleteDirectory(pathName);
        }

        return new RemoveResult(pathName, childDirectories);
    }


    /**
     * 자식 디렉토리가 있는지 확인하고 옵션 따라 계속 진행 혹은 예외 처리(ExceptionHandler -> 실패 응답)
     * @return 자식 디렉토리 함께 지울지 여부
     */
    private boolean checkDeleteWithChild(boolean isRemoveWithChild, List<String> childDirectories) {
        boolean isDeleteWithChild = false;
        if (!childDirectories.isEmpty()) {
            if (!isRemoveWithChild) {
                throw new PredictableRuntimeException("자식 디렉토리가 존재합니다. 함께 삭제하길 원하실 경우 'renameWithChild=true' 옵션을 쿼리스트링으로 요청해주십시오.");
            }
            isDeleteWithChild = true;
        }
        return isDeleteWithChild;
    }

}
