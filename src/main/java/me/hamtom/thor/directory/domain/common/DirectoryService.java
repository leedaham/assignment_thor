package me.hamtom.thor.directory.domain.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.hamtom.thor.directory.domain.common.directory.dto.ChildDirectoriesInfoDto;
import me.hamtom.thor.directory.domain.common.directory.dto.PathDetailDto;
import me.hamtom.thor.directory.domain.common.directory.dto.ParentDirectoriesInfoDto;
import me.hamtom.thor.directory.domain.common.directory.entity.Directory;
import me.hamtom.thor.directory.domain.common.directory.repository.DirectoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DirectoryService {
    private final DirectoryRepository directoryRepository;

    /**
     * 디렉토리 저장
     * @param pathName
     * @param owner
     * @param group
     * @param permissions
     * @param size
     * @return 저장 디렉토리 pathName
     */
    @Transactional
    public String saveDirectory(String pathName, String owner, String group, String permissions, int size) {
        Directory directory = Directory.createDirectory(pathName, owner, group, permissions, size);
        return directoryRepository.save(directory).getPathName();
    }

    /**
     * 여러 디렉토리 저장
     * @param pathNames
     * @param owner
     * @param group
     * @param permissions
     * @param size
     * @return 저장 디렉토리 pathName list
     */
    @Transactional
    public List<String> saveDirectories(List<String> pathNames, String owner, String group, String permissions, int size) {
        List<String> saveDirectories = new ArrayList<>();
        for (String pathName : pathNames) {
            Directory directory = Directory.createDirectory(pathName, owner, group, permissions, size);
            Directory save = directoryRepository.save(directory);
            saveDirectories.add(save.getPathName());
        }
        return saveDirectories;
    }

    /**
     * 디렉토리 이름 변경
     * @param newPathName
     * @param oldPathName
     * @return update count
     */
    @Transactional
    public int renameDirectory(String newPathName, String oldPathName) {
        return (int) directoryRepository.renameDirectory(newPathName, oldPathName);
    }

    /**
     * 여러 디렉토리 이름 변경
     * @param renameList
     * @return update count
     */
    @Transactional
    public int renameDirectoies(Map<String, String> renameList) {
        int count = 0;
        for (Map.Entry<String, String> entry : renameList.entrySet()) {
            count += directoryRepository.renameDirectory(entry.getKey(), entry.getValue());
        }
        return count;
    }

    @Transactional
    public void deleteDirectory(String pathName) {
        directoryRepository.deleteByPathName(pathName);
    }

    /**
     * 디렉토리 존재 확인
     * @param pathName
     * @return 존재 여부
     */
    public boolean isDirectoryExist(String pathName) {
        int count = directoryRepository.countByPathName(pathName);
        return (count > 0);
    }

    /**
     * 디렉토리 경로 정보
     * @param pathName
     * @return 디렉토리 경로 구체적 정보 (디렉토리 경로, 디렉토리 layer, 디렉토리 이름, 경로)
     */
    public PathDetailDto getPathDetail(String pathName) {
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

        return new PathDetailDto(pathName, layerCount, dirName, layers);
    }

    /**
     * 부모 디렉토리 존재 확인
     * @param layers 디렉토리 계층 정보
     * @return MissingParentInfoDto - 누락된 부모 디렉토리 정보
     */
    public ParentDirectoriesInfoDto getParentDirectoriesInfo(String pathName, List<String> layers) {
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

        return new ParentDirectoriesInfoDto(pathName, missingDirectories, existingDirectories, orphanedDirectories);
    }

    public int getUsedCapacity() {
        Integer allUsedCapacity = directoryRepository.getAllUsedCapacity();
        if (allUsedCapacity == null) {
            return 0;
        } else {
            return allUsedCapacity;
        }
    }

    public ChildDirectoriesInfoDto getChildDirectoriesInfo(String oldPathName) {
        List<String> childDirectoriesPathName = directoryRepository.getChildDirectoriesPathName(oldPathName);
        childDirectoriesPathName.remove(oldPathName);

        return new ChildDirectoriesInfoDto(oldPathName, childDirectoriesPathName);
    }
    private int getLayerNum(String pathName) {
        return (int) pathName.chars().filter(c -> c == '/').count();
    }
}
