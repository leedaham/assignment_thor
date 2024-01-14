package me.hamtom.thor.directory.domain.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 부모 디렉토리 중 누락된 디렉토리 확인
 */
@Data
@AllArgsConstructor
public class ParentDirectoriesInfoDto {
    private String pathName;
    private List<String> missingDirectories;
    private List<String> existingDirectories;

    public boolean hasMissingParent() {
        return !missingDirectories.isEmpty();
    }

    public int countMissingParent() {
        return missingDirectories.size();
    }
}
