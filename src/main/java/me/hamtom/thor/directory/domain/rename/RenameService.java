package me.hamtom.thor.directory.domain.rename;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.hamtom.thor.directory.domain.common.DirectoryService;
import me.hamtom.thor.directory.domain.common.directory.dto.ChildDirectoriesInfoDto;
import me.hamtom.thor.directory.domain.common.enumerated.OptionValue;
import me.hamtom.thor.directory.domain.common.exception.PredictableRuntimeException;
import me.hamtom.thor.directory.domain.rename.dto.RenameDirectoryDto;
import me.hamtom.thor.directory.domain.rename.dto.RenameDirectoryResultDto;
import me.hamtom.thor.directory.domain.rename.enumerated.RenameType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RenameService {
    private final DirectoryService directoryService;

    public RenameDirectoryResultDto renameDirectory(RenameDirectoryDto renameDirectoryDto) {
        //rename 정보 가져오기
        String oldPathName = renameDirectoryDto.getOldPathName();
        String newName = renameDirectoryDto.getNewName();
        OptionValue renameWithChild = renameDirectoryDto.getRenameWithChild();
        OptionValue mergeOnDuplicate = renameDirectoryDto.getMergeOnDuplicate();

        //oldPath 존재 확인
        checkOldPathNameExist(oldPathName);

        //oldPath 자식 디렉토리 정보 가져오기
        ChildDirectoriesInfoDto oldPathChildDirectoriesInfoDto = directoryService.getChildDirectoriesInfo(oldPathName);
        List<String> oldPathChildDirectories = oldPathChildDirectoriesInfoDto.getChildDirectories();

        //자식 디렉토리 존재 -> 옵션 (renameWithChild) 확인
        ifChildExist(renameWithChild, oldPathChildDirectories);

        //newPathName
        String newPathName = generateNewPathName(oldPathName, newName);

        //newPath 중복 및 병합 여부 확인
        boolean isMerge = checkDuplicateNewPathForMerge(mergeOnDuplicate, newPathName);

        //바꿀 이름 (newPathName, oldPathName)
        Map<String, String> renameMap = generateRenameMap(oldPathName, oldPathChildDirectories, newPathName);

        //newPath 자식 디렉토리 확인
        ChildDirectoriesInfoDto newPathChildDirectoriesInfoDto = directoryService.getChildDirectoriesInfo(newPathName);
        List<String> newPathChildDirectories = newPathChildDirectoriesInfoDto.getChildDirectories();

        //Rename, Merge 분기
        RenameType renameType = RenameType.RENAME;
        if (isMerge) {
            //renameMap 에서 이미 존재하는 디렉토리 지우기 (병합)
            String removePath = renameMap.get(newPathName);
            directoryService.deleteDirectory(removePath);
            renameMap.remove(newPathName);

            for (String newPathChildDirectory : newPathChildDirectories) {
                removePath = renameMap.get(newPathChildDirectory);
                directoryService.deleteDirectory(removePath);
                renameMap.remove(newPathChildDirectory);
            }
            renameType = RenameType.MERGE;
        }

        //rename
        directoryService.renameDirectoies(renameMap);

        List<String> renamedDirectories = renameMap.keySet().stream().toList();
        return new RenameDirectoryResultDto(renameType, oldPathName, newPathName, renamedDirectories, newPathChildDirectories);
    }

    private Map<String, String> generateRenameMap(String oldPathName, List<String> oldPathChildDirectories, String newPathName) {
        List<String> renameList = new ArrayList<>(oldPathChildDirectories);
        renameList.add(oldPathName);
        return renameList
                .stream()
                .collect(Collectors.toMap(
                        key -> key.replaceFirst(oldPathName, newPathName),
                        value -> value
                ));
    }

    private String generateNewPathName(String oldPathName, String newName) {
        int lastIndexOf = oldPathName.lastIndexOf('/');
        String newPathName = oldPathName.substring(0, lastIndexOf + 1) + newName;
        return newPathName;
    }

    private void ifChildExist(OptionValue renameWithChild, List<String> oldPathChildDirectories) {
        if (!oldPathChildDirectories.isEmpty()) {
            if (renameWithChild.equals(OptionValue.FALSE)) {
                throw new PredictableRuntimeException("자식 디렉토리가 존재합니다. 함께 이름을 변경하길 원하실 경우 'renameWithChild=T' 옵션을 쿼리스트링으로 요청해주십시오.");
            }
        }
    }

    private boolean checkDuplicateNewPathForMerge(OptionValue mergeOnDuplicate, String newPathName) {
        boolean directoryExist = directoryService.isDirectoryExist(newPathName);
        //존재 -> 옵션 확인
        if (directoryExist) {
            log.info("변경하고자 하는 pathName 중복.");

            //옵션값 FALSE -> 실패 응답
            if(mergeOnDuplicate.equals(OptionValue.FALSE)){
                throw new PredictableRuntimeException("변경하고자 하는 이름의 디렉토리가 이미 존재 합니다. 병합히길 원하시는 경우 'mergeOnDuplicate=F' 옵션을 쿼리스트링으로 요청해주십시오.");
            }

            //옵션값 TRUE
            return true;
        }
        return false;
    }

    private void checkOldPathNameExist(String oldPathName) {
        boolean directoryExist = directoryService.isDirectoryExist(oldPathName);
        if (!directoryExist) {
            throw new PredictableRuntimeException("존재하지 않는 디렉토리입니다.");
        }
        log.info("기존 디렉토리 존재. oldPathName: {}", oldPathName);
    }
}
