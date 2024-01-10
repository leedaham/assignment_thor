package me.hamtom.thor.directory.domain.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.hamtom.thor.directory.domain.common.directory.dto.DirectoryPathInfoDto;
import me.hamtom.thor.directory.domain.common.directory.dto.ParentDirectoriesInfoDto;
import me.hamtom.thor.directory.domain.common.directory.entity.Directory;
import me.hamtom.thor.directory.domain.common.directory.repository.DirectoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DirectoryService {
    private final DirectoryRepository directoryRepository;

    public Directory saveDirectory(Directory directory) {
        return directoryRepository.save(directory);
    }

    public boolean isDirectoryExist(String pathName) {
        int count = directoryRepository.countByPathName(pathName);
        return (count > 0);
    }

    public DirectoryPathInfoDto getDirectoryPathInfo(String pathName) {
        int layerCount = getLayerNum(pathName);
        int lastSlashIndex = pathName.lastIndexOf('/');

        if (layerCount < 1) {
            throw new RuntimeException("경로가 잘못되었습니다. 다시 시도하십시오. 문제가 계속 발생하면 관리자에게 문의하십시오.");
        }

        // 디렉토리 명
        String dirName = pathName.substring(lastSlashIndex + 1);

        // 각 계층 배열
        String[] splitPath = pathName.split("/");
        List<String> layers = Arrays.stream(splitPath)
                .skip(1) // 첫 번째 빈 문자열을 제거
                .toList();

        return new DirectoryPathInfoDto(pathName, layerCount, dirName, layers);
    }

    /**
     * 부모 디렉토리 존재 확인
     * @param layers 디렉토리 계층 정보
     * @return MissingParentInfoDto - 누락된 부모 디렉토리 정보
     */
    public ParentDirectoriesInfoDto getParentDirectoriesInfo(String childDirectory, List<String> layers) {
        List<String> missingDirectories = new ArrayList<>();
        List<String> existingDirectories = new ArrayList<>();
        List<String> orphanedDirectories = new ArrayList<>();

        List<String> onlyParents = new ArrayList<>(layers);

        // 부모 요소만 남기기 (Child Dir 제거)
        onlyParents.remove(onlyParents.size() - 1);

        // 부모 디렉토리 모두 경로화 하기
        List<String> allParentPaths = new ArrayList<>();
        StringBuilder path = new StringBuilder();
        for (String layer : onlyParents) {
            path.append("/").append(layer);
            allParentPaths.add(path.toString());
        }

        // 부모 디렉토리 누락 유무 확인
        for (String parentPath : allParentPaths) {
            if (isDirectoryExist(parentPath)) {
                // 존재
                existingDirectories.add(parentPath);
            }else{
                // 존재 X
                missingDirectories.add(parentPath);
            }
        }

        // 고아 디렉토리 유무 확인
        if (!missingDirectories.isEmpty()) {
            int firstMissingLayerNum = getLayerNum(missingDirectories.get(0));
            orphanedDirectories = existingDirectories.stream().filter(d -> getLayerNum(d) > firstMissingLayerNum).toList();
            existingDirectories.removeAll(orphanedDirectories);
        }

        return new ParentDirectoriesInfoDto(childDirectory, missingDirectories, existingDirectories, orphanedDirectories);
    }

    @Transactional
    public void removeOrphanedDirectory(List<String> orphanedDirectories) {
        if (orphanedDirectories.isEmpty()) {
            return;
        }

        for (String orphanedDirectory : orphanedDirectories) {
            directoryRepository.deleteByPathName(orphanedDirectory);
            log.info("고아 디렉토리 삭제, path: {}", orphanedDirectory);
        }
    }

    public int getUsedCapacity() {
        Integer allUsedCapacity = directoryRepository.getAllUsedCapacity();
        if (allUsedCapacity == null) {
            return 0;
        } else {
            return allUsedCapacity;
        }
    }

    private int getLayerNum(String pathName) {
        return (int) pathName.chars().filter(c -> c == '/').count();
    }
}
