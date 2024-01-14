package me.hamtom.thor.directory.domain.sublist;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.hamtom.thor.directory.domain.common.DirectoryService;
import me.hamtom.thor.directory.domain.common.dto.ChildDirectoriesInfoDto;
import me.hamtom.thor.directory.domain.sublist.dto.GetSublistCommand;
import me.hamtom.thor.directory.domain.sublist.dto.GetSublistResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetSublistService {

    private final DirectoryService directoryService;

    /**
     * 디렉토리 서브리스트 조회 메소드
     * @return 디렉토리 서브리스트
     */
    public GetSublistResult getSublist(GetSublistCommand command) {
        String pathName = command.getPathName();

        // "/" 검색하면 전체 디렉토리 검색
        if (!pathName.equals("/")) {
            directoryService.checkExistPathName(pathName);
        }
        log.info("서브리스트 조회. pathName: {}", pathName);

        // 검색 디렉토리 자식 디렉토리 정보
        ChildDirectoriesInfoDto childDirectoriesInfo = directoryService.getChildDirectoriesInfo(pathName);
        List<String> childDirectories = childDirectoriesInfo.getChildDirectories();

        // 자식 디렉토리 정보 정리
        Map<Integer, List<String>> map = new HashMap<>();
        for (String childDirectory : childDirectories) {
            int layerNum = directoryService.getLayerNum(childDirectory);
            map.computeIfAbsent(layerNum, v -> new ArrayList<>()).add(childDirectory);
        }

        return convertMapToSublist(map, pathName);
    }

    /**
     * 자식 디렉토리를 순회하면서 서브리스트 추가
     */
    private GetSublistResult convertMapToSublist(Map<Integer, List<String>> map, String rootName) {
        GetSublistResult root = new GetSublistResult(rootName);

        for (Map.Entry<Integer, List<String>> entry : map.entrySet()) {
            int childLayer = entry.getKey();
            List<String> childPaths = entry.getValue();

            for (String subDirectory : childPaths) {
                insertSubDirectory(root, subDirectory, childLayer);
            }
        }

        return root;
    }

    /**
     * 디렉토리 경로를 /로 분리하여 각각 자식 디렉토리 확인 및 생성/추가
     */
    private void insertSubDirectory(GetSublistResult root, String subDirectory, int depth) {
        GetSublistResult current = root;

        String[] parts = subDirectory.split("/");
        for (int i = 1; i <= depth; i++) {
            String path = "/";
            for (int j = 1; j <= i; j++) {
                path += parts[j];
                if (j < i) {
                    path += "/";
                }
            }
            GetSublistResult child = findOrCreateChild(current, path);
            current = child;
        }
    }

    /**
     * 확인하는 디렉토리가 자식 디렉토리인지 확인 및 생성/추가
     */
    private GetSublistResult findOrCreateChild(GetSublistResult parent, String name) {
        String parentName = parent.getName();
        if (parentName.equals(name)) {
            return parent;
        }
        for (GetSublistResult child : parent.getSubDirectories()) {
            if (child.getName().equals(name)) {
                return child;
            }
        }

        GetSublistResult newChild = new GetSublistResult(name);
        parent.getSubDirectories().add(newChild);
        return newChild;
    }
}
