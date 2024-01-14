package me.hamtom.thor.directory.domain.remove;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.hamtom.thor.directory.domain.common.DirectoryService;
import me.hamtom.thor.directory.domain.common.dto.ChildDirectoriesInfoDto;
import me.hamtom.thor.directory.domain.common.exception.PredictableRuntimeException;
import me.hamtom.thor.directory.domain.remove.dto.RemoveCommand;
import me.hamtom.thor.directory.domain.remove.dto.RemoveDirectoryResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RemoveService {

    private final DirectoryService directoryService;

    @Transactional
    public RemoveDirectoryResult removeDirectory(RemoveCommand command) {
        String pathName = command.getPathName();
        boolean isRemoveWithChild = command.isRemoveWithChild();

        //디렉토리 경로 존재 확인. 존재 X -> 실패 응답
        directoryService.checkExistPathName(pathName);

        //자식 디렉토리 확인
        ChildDirectoriesInfoDto childDirectoriesInfo = directoryService.getChildDirectoriesInfo(pathName);
        List<String> childDirectories = childDirectoriesInfo.getChildDirectories();

        //자식과 함께 삭제 확인
        boolean isDeleteWithChild = checkDeleteWithChild(isRemoveWithChild, childDirectories);

        //디렉토리 지우기
        if (isDeleteWithChild) {
            directoryService.deleteDirectoryWithChild(pathName);
        } else {
            directoryService.deleteDirectory(pathName);
        }

        return new RemoveDirectoryResult(pathName, childDirectories);
    }

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
