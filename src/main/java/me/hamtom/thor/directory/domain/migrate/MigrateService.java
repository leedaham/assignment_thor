package me.hamtom.thor.directory.domain.migrate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.hamtom.thor.directory.domain.common.DirectoryService;
import me.hamtom.thor.directory.domain.common.dto.ChildDirectoriesInfoDto;
import me.hamtom.thor.directory.domain.migrate.dto.MergeInfoDto;
import me.hamtom.thor.directory.domain.migrate.dto.MigrateCommand;
import me.hamtom.thor.directory.domain.migrate.dto.MigrateResult;
import me.hamtom.thor.directory.domain.common.entity.Directory;
import me.hamtom.thor.directory.domain.migrate.enumerated.MergeStatus;
import me.hamtom.thor.directory.domain.common.exception.PredictableRuntimeException;
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
public class MigrateService {
    private final DirectoryService directoryService;

    public MigrateResult migrateDirectory(MigrateCommand command) {
        //command 변수
        String sourcePath = command.getSourcePath();
        String toMigratePath = command.getToMigratePath();
        boolean mergeOnDuplicate = command.isMergeOnDuplicate();

        //sourcePath == toMigratePath ?
        checkToMigratePathAvailable(sourcePath, toMigratePath);

        //sourcePath exist ?
        Directory sourcePathDirectory = directoryService.checkExistAndGetDirectory(sourcePath);

        //toMigratePath 부모 존재 ?
        checkToMigratePathParentExist(toMigratePath);

        //sourcePath hasChild ?
        ChildDirectoriesInfoDto sourcePathChildInfo = directoryService.getChildDirectoriesInfo(sourcePath);
        boolean hasChild = sourcePathChildInfo.hasChild();

        // toMigratePath duplicate, check merge option
        MergeStatus mergeStatus = ifNeedMergeCheckOption(toMigratePath, mergeOnDuplicate);

        //자식 없음
        if (!hasChild) {
            //병합시
            if (mergeStatus.equals(MergeStatus.NOT_MERGE)) {
                sourcePathDirectory.changePathName(toMigratePath);
            }
            //병합X
            else if (mergeStatus.equals(MergeStatus.MERGE)) {
                directoryService.deleteDirectory(sourcePath);
            }
            return new MigrateResult(sourcePath, toMigratePath, mergeStatus);
        }

        //자식 있음
        else {
            //병합, 병합 X 공통
            List<String> sourcePathChild = sourcePathChildInfo.getChildDirectories();
            Map<String, String> migrationMap = generateMigrationMap(sourcePath, sourcePathChild, toMigratePath);
            List<String> mergedList = new ArrayList<>();

            //병합시
            if (mergeStatus.equals(MergeStatus.MERGE)) {
                ChildDirectoriesInfoDto toMigratePathChildInfo = directoryService.getChildDirectoriesInfo(toMigratePath);
                List<String> toMigratePathChild = toMigratePathChildInfo.getChildDirectories();

                MergeInfoDto mergeInfo = generateMergeInfo(toMigratePath, migrationMap, toMigratePathChild);

                migrationMap = mergeInfo.getMigrationMap();
                mergedList = mergeInfo.getMergedList();
            }

            directoryService.updateDirectoriesPathName(migrationMap);
            List<String> migratedList = migrationMap.keySet().stream().filter(m -> !m.equals(toMigratePath)).toList();
            return new MigrateResult(sourcePath, toMigratePath, mergeStatus, migratedList, mergedList);
        }
    }

    private void checkToMigratePathParentExist(String toMigratePath) {
        long count = toMigratePath.chars().filter(ch -> ch == '/').count();
        if (count == 1) {
            return;
        }
        int lastSlashIndex = toMigratePath.lastIndexOf('/');
        String toMigratePathParent = toMigratePath.substring(0, lastSlashIndex);
        if (!toMigratePathParent.equals("/")) {
            directoryService.checkExistPathName(toMigratePathParent);
        }
    }

    public String generateToMigratePathForMove(String sourcePath, String targetPath) {
        //sourcePath == "/" ?
        ifSourcePathRootPath(sourcePath);

        String[] parts = sourcePath.split("/");
        String dirName = parts[parts.length - 1];

        boolean isTargetPathRootPath = directoryService.isRootPath(targetPath);
        if (isTargetPathRootPath) {
            return "/" + dirName;
        } else {
            return targetPath + "/" + dirName;
        }
    }

    public String generateToMigratePathForRename(String oldPathName, String newName) {
        //oldPathName == "/" ?
        ifSourcePathRootPath(oldPathName);

        int lastIndexOf = oldPathName.lastIndexOf('/');
        return oldPathName.substring(0, lastIndexOf + 1) + newName;
    }

    private void ifSourcePathRootPath(String path) {
        boolean isRootPath = directoryService.isRootPath(path);
        if (isRootPath) {
            throw new PredictableRuntimeException("root 경로(/)입니다. 변경할 수 없습니다.");
        }
    }

    private void checkToMigratePathAvailable(String sourcePath, String toMigratePath) {
        if (sourcePath.equals(toMigratePath)) {
            throw new PredictableRuntimeException("변경할 경로와 현재 경로가 일치합니다.");
        }
    }

    private MergeStatus ifNeedMergeCheckOption(String toMigratePath, boolean mergeOnDuplicate) {
        boolean needMerge = directoryService.isDirectoryExist(toMigratePath);
        if (needMerge) {
            if (!mergeOnDuplicate) {
                throw new PredictableRuntimeException("변경하고자 하는 경로에 디렉토리가 이미 존재 합니다. 병합하길 원하시는 경우 'mergeOnDuplicate=true' 옵션을 쿼리스트링으로 요청해주십시오.");
            }
            return MergeStatus.MERGE;
        }
        return MergeStatus.NOT_MERGE;
    }

    private Map<String, String> generateMigrationMap(String sourcePath, List<String> sourcePathChild, String toMigratePath) {
        sourcePathChild.add(sourcePath);
        return sourcePathChild
                .stream()
                .collect(Collectors.toMap(
                        key -> key.replaceFirst(sourcePath, toMigratePath),
                        value -> value
                ));
    }

    private MergeInfoDto generateMergeInfo(String toMigratePath, Map<String, String> migrationMap, List<String> compareForMerge) {
        //migrationMap 에는 pathName 변경할 목록만 남기기
        //병합으로 중복될 pathName 디렉토리는 deleteDirectory
        String removePath = migrationMap.get(toMigratePath);
        directoryService.deleteDirectory(removePath);
        migrationMap.remove(toMigratePath);

        List<String> mergedList = new ArrayList<>();
        for (String nameToCompare : compareForMerge) {
            removePath = migrationMap.getOrDefault(nameToCompare, null);
            if (removePath == null) {
                continue;
            }

            removePath = migrationMap.get(nameToCompare);
            directoryService.deleteDirectory(removePath);
            migrationMap.remove(nameToCompare);

            mergedList.add(nameToCompare);
        }
        return new MergeInfoDto(migrationMap, mergedList);
    }
}
