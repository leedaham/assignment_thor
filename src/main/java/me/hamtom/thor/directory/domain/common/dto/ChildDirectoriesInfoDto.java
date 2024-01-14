package me.hamtom.thor.directory.domain.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 부모 디렉토리 중 누락된 디렉토리 확인
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
