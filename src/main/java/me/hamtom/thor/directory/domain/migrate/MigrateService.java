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

    /**
     * 디렉토리 이동, 이름 변경 메서드,
     * 시작 경로에서 목적 경로로 디렉토리 pathName 변경
     * @return 디렉토리 pathName 변경 결과
     */
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

        log.info("디렉토리 변경. sourcePath: {}, toMigratePath{}", sourcePath, toMigratePath);
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

    /**
     * 디렉토리 이동시 목적 경로(toMigratePath) 생성 메소드
     * @return 목적 경로(toMigratePath)
     */
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

    /**
     * 디렉토리 이름 변경시 목적 경로 (toMigratePath) 생성 메소드
     * @return 목적 경로(toMigratePath)
     */
    public String generateToMigratePathForRename(String oldPathName, String newName) {
        //oldPathName == "/" ?
        ifSourcePathRootPath(oldPathName);

        int lastIndexOf = oldPathName.lastIndexOf('/');
        return oldPathName.substring(0, lastIndexOf + 1) + newName;
    }

    /**
     * 목적 경로의 부모 디렉토리 존재 여부에 따라 계속 진행 혹은 예외 처리(ExceptionHandler -> 실패 응답)
     */
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

    /**
     * 변경을 원하는 경로가 root 경우 예외 처리(ExceptionHandler -> 실패 응답)
     * @param path
     */
    private void ifSourcePathRootPath(String path) {
        boolean isRootPath = directoryService.isRootPath(path);
        if (isRootPath) {
            throw new PredictableRuntimeException("root 경로(/)입니다. 변경할 수 없습니다.");
        }
    }

    /**
     * 변경을 원하는 경로와 목적 경로가 같을 경우 예외 처리(ExceptionHandler -> 실패 응답)
     */
    private void checkToMigratePathAvailable(String sourcePath, String toMigratePath) {
        if (sourcePath.equals(toMigratePath)) {
            throw new PredictableRuntimeException("변경할 경로와 현재 경로가 일치합니다.");
        }
    }

    /**
     * 목적 경로가 이미 존재하는지 확인하고 옵션 따라 계속 진행 혹은 예외 처리(ExceptionHandler -> 실패 응답)
     * @return 계속 진행할 경우 병합 여부
     */
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

    /**
     * 목적 경로, 변경을 원하는 경로 매핑
     * @return 변경 정보 map
     */
    private Map<String, String> generateMigrationMap(String sourcePath, List<String> sourcePathChild, String toMigratePath) {
        sourcePathChild.add(sourcePath);
        return sourcePathChild
                .stream()
                .collect(Collectors.toMap(
                        key -> key.replaceFirst(sourcePath, toMigratePath),
                        value -> value
                ));
    }

    /**
     * 병합으로 중복될 경로는 삭제하고, update pathName 할 목록 생성
     * @return update pathName 할 목록
     */
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
