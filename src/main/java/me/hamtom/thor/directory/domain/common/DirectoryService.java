package me.hamtom.thor.directory.domain.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.hamtom.thor.directory.domain.common.dto.ChildDirectoriesInfoDto;
import me.hamtom.thor.directory.domain.common.dto.ParentDirectoriesInfoDto;
import me.hamtom.thor.directory.domain.common.entity.Directory;
import me.hamtom.thor.directory.domain.common.repository.DirectoryRepository;
import me.hamtom.thor.directory.domain.common.exception.PredictableRuntimeException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DirectoryService {
    private final DirectoryRepository directoryRepository;

    /**
     * 디렉토리 저장
     * @return 저장 디렉토리 pathName
     */
    @Transactional
    public String saveDirectory(String pathName, String owner, String group, String permissions, int size) {
        Directory directory = Directory.createDirectory(pathName, owner, group, permissions, size);
        return directoryRepository.save(directory).getPathName();
    }

    /**
     * 여러 디렉토리 저장
     * @return 저장 디렉토리 pathName list
     */
    @Transactional
    public List<String> saveDirectories(List<String> pathNames, String owner, String group, String permissions, int size) {
        List<Directory> saveDirectories = new ArrayList<>();
        for (String pathName : pathNames) {
            Directory directory = Directory.createDirectory(pathName, owner, group, permissions, size);
            saveDirectories.add(directory);
        }
        List<Directory> savedDirectories = directoryRepository.saveAll(saveDirectories);
        return savedDirectories.stream().map(Directory::getPathName).toList();
    }

    /**
     * 여러 디렉토리 pathName 변경
     */
    @Transactional
    public void updateDirectoriesPathName(Map<String, String> migrationMap) {
        for (Map.Entry<String, String> entry : migrationMap.entrySet()) {
            directoryRepository.updateDirectoryPathName(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 디렉토리 삭제
     */
    @Transactional
    public void deleteDirectory(String pathName) {
        directoryRepository.deleteByPathName(pathName);
    }

    /**
     * 디렉토리 삭제 (자식 디렉토리 함께 삭제)
     */
    @Transactional
    public void deleteDirectoryWithChild(String pathName) {
        directoryRepository.deleteDirectoryWithChild(pathName);
    }

    /**
     * 디렉토리 존재 확인
     * @return 존재 여부
     */
    public boolean isDirectoryExist(String pathName) {
        return directoryRepository.isExist(pathName);
    }

    /**
     * 디렉토리 경로가 비어있는지 확인
     */
    public void checkAvailablePathName(String pathName){
        boolean directoryExist = directoryRepository.isExist(pathName);
        if (directoryExist) {
            String msg = String.format("이미 존재하는 경로입니다. 경로: %s", pathName);
            throw new PredictableRuntimeException(msg);
        }
    }

    /**
     * 디렉토리 경로가 사용중인지 확인
     */
    public void checkExistPathName(String pathName){
        boolean directoryExist = directoryRepository.isExist(pathName);
        if (!directoryExist) {
            String msg = String.format("존재하지 않는 경로입니다. 경로: %s", pathName);
            throw new PredictableRuntimeException(msg);
        }
    }

    /**
     * 디렉토리 검색
     * @return 디렉토리
     */
    public Directory checkExistAndGetDirectory(String pathName) {
        Optional<Directory> byPathName = directoryRepository.findByPathName(pathName);
        if (byPathName.isEmpty()) {
            String msg = String.format("존재하지 않는 경로입니다. 경로: %s", pathName);
            throw new PredictableRuntimeException(msg);
        }
        return byPathName.get();
    }

    /**
     * 부모 디렉토리 정보 확인
     * @return ParentDirectoriesInfoDto - 부모 디렉토리 정보
     */
    public ParentDirectoriesInfoDto getParentDirectoriesInfo(String pathName) {
        List<String> existingDirectories = new ArrayList<>();
        List<String> missingDirectories = new ArrayList<>();

        // 부모 디렉토리 경로 구하기
        List<String> parentPathList = new ArrayList<>();
        String[] pathSegments = pathName.split("/");

        StringBuilder currentPath = new StringBuilder();
        for (int i = 1; i < pathSegments.length - 1; i++) {
            currentPath.append("/").append(pathSegments[i]);
            parentPathList.add(currentPath.toString());
        }

        // 부모 디렉토리 누락 유무 확인
        for (String parentPath : parentPathList) {
            boolean directoryExist = directoryRepository.isExist(parentPath);
            if (directoryExist) {
                // 존재
                existingDirectories.add(parentPath);
            }else{
                // 존재 X
                missingDirectories.add(parentPath);
            }
        }

        return new ParentDirectoriesInfoDto(pathName, missingDirectories, existingDirectories);
    }

    /**
     * 자식 디렉토리 정보 확인
     * @return ChildDirectoriesInfoDto - 자식 디렉토리 정보
     */
    public ChildDirectoriesInfoDto getChildDirectoriesInfo(String pathName) {
        List<String> childDirectoriesPathName = directoryRepository.getChildDirectoriesPathName(pathName);
        childDirectoriesPathName.remove(pathName);

        return new ChildDirectoriesInfoDto(pathName, childDirectoriesPathName);
    }

    /**
     * 현재 사용중인 용량 구하기
     * @return 현재 사용중인 용량
     */
    public int getUsedCapacity() {
        Integer allUsedCapacity = directoryRepository.getAllUsedCapacity();
        if (allUsedCapacity == null) {
            return 0;
        } else {
            return allUsedCapacity;
        }
    }

    /**
     * 경로로부터 디렉토리 layer 구하기
     * @param pathName
     * @return 디렉토리 layer
     */
    public int getLayerNum(String pathName) {
        return (int) pathName.chars().filter(c -> c == '/').count();
    }

    /**
     * 경로가 root 경로인지 확인
     * @return root 경로 여부
     */
    public boolean isRootPath(String path) {
        return path.equals("/");
    }

    /**
     * 생성/삭제/조회 경로가 root 경우 예외 처리(ExceptionHandler -> 실패 응답)
     */
    public void throwExceptionIfRootPath(String pathName) {
        boolean isRootPath = isRootPath(pathName);
        if (isRootPath) {
            throw new PredictableRuntimeException("root 경로(/)입니다. [생성/삭제/조회]할 수 없습니다.");
        }
    }
}
