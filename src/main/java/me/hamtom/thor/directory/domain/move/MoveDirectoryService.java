package me.hamtom.thor.directory.domain.move;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.hamtom.thor.directory.domain.common.DirectoryService;
import me.hamtom.thor.directory.domain.common.directory.dto.ChildDirectoriesInfoDto;
import me.hamtom.thor.directory.domain.common.enumerated.OptionValue;
import me.hamtom.thor.directory.domain.common.exception.PredictableRuntimeException;
import me.hamtom.thor.directory.domain.move.dto.MoveDirectoryCommand;
import me.hamtom.thor.directory.domain.move.dto.MoveDirectoryResult;
import me.hamtom.thor.directory.domain.move.dto.enumerated.MoveType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MoveDirectoryService {
    private final DirectoryService directoryService;

    public MoveDirectoryResult moveDirectory(MoveDirectoryCommand command) {
        String sourcePath = command.getSourcePath();
        String targetPath = command.getTargetPath();
        OptionValue moveWithChild = command.getMoveWithChild();
        OptionValue mergeOnDuplicate = command.getMergeOnDuplicate();

        // 1) 출발 디렉토리가 존재하는 디렉토리인가?
        directoryService.checkExistPathName(sourcePath);
        
        // 2) 자식이 있는가? 있다면 옵션 확인
        ChildDirectoriesInfoDto sourcePateChildDirectoriesInfo = directoryService.getChildDirectoriesInfo(sourcePath);
        List<String> sourcePathChildDirectories = sourcePateChildDirectoriesInfo.getChildDirectories();
        if (!sourcePathChildDirectories.isEmpty() && (moveWithChild.equals(OptionValue.FALSE))) {
                throw new PredictableRuntimeException("자식 디렉토리가 존재합니다. 함께 이동하길 원하실 경우 'moveWithChild=T' 옵션을 쿼리스트링으로 요청해주십시오.");
        }

        // 이동할 경로로 변경
        String toMovePath = generateToMovePath(sourcePath, targetPath);

        // 3) 도착 디렉토리가 존재하는가? 존재한다면 옵션 확인 
        boolean isMerge = checkDuplicateTargetPathForMerge(toMovePath, mergeOnDuplicate);

        //이동할 목록
        Map<String, String> moveMap = generateMoveMap(sourcePath, sourcePathChildDirectories, toMovePath);

        //targetPath 자식 디렉토리 확인
        ChildDirectoriesInfoDto toMovePathChildDirectoriesInfo = directoryService.getChildDirectoriesInfo(toMovePath);
        List<String> toMovePathChildDirectories = toMovePathChildDirectoriesInfo.getChildDirectories();

        //Rename, Merge 분기
        MoveType moveType = MoveType.MOVE;
        if (isMerge) {
            //moveMap 에서 이미 존재하는 디렉토리 지우기 (병합)
            String removePath = moveMap.get(toMovePath);
            directoryService.deleteDirectory(removePath);
            moveMap.remove(toMovePath);

            for (String toMovePathChildDirectory : toMovePathChildDirectories) {
                removePath = moveMap.get(toMovePathChildDirectory);
                directoryService.deleteDirectory(removePath);
                moveMap.remove(toMovePathChildDirectory);
            }
            moveType = MoveType.MERGE;
        }

        // 4) 이동

        directoryService.updateDirectoriesPathName(moveMap);

        List<String> movedDirectories = moveMap.keySet().stream().toList();
        return new MoveDirectoryResult(moveType, sourcePath, targetPath, toMovePath, movedDirectories, toMovePathChildDirectories);
    }

    private boolean checkDuplicateTargetPathForMerge(String targetPath, OptionValue mergeOnDuplicate) {
        boolean targetDirectoryExist = directoryService.isDirectoryExist(targetPath);
        if (targetDirectoryExist) {
            if (mergeOnDuplicate.equals(OptionValue.FALSE)) {
                throw new PredictableRuntimeException("이동하고자 하는 경로에 디렉토리가 이미 존재 합니다. 병합히길 원하시는 경우 'mergeOnDuplicate=T' 옵션을 쿼리스트링으로 요청해주십시오.");
            }
            return true;
        }
        return false;
    }

    private String generateToMovePath(String sourcePath, String targetPath) {
        return targetPath + sourcePath;
    }

    private Map<String, String> generateMoveMap(String sourcePath, List<String> sourcePathChildDirectories, String toMovePath) {
        List<String> renameList = new ArrayList<>(sourcePathChildDirectories);
        renameList.add(sourcePath);
        return renameList
                .stream()
                .collect(Collectors.toMap(
                        key -> key.replaceFirst(sourcePath, toMovePath),
                        value -> value
                ));
    }
}
