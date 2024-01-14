package me.hamtom.thor.directory.domain.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 자식 디렉토리 정보 확인
 */
@Data
@AllArgsConstructor
public class ChildDirectoriesInfoDto {
    private String pathName;
    private List<String> childDirectories;

    public boolean hasChild() {
        return !childDirectories.isEmpty();
    }

    public int countChild() {
        return childDirectories.size();
    }
}
