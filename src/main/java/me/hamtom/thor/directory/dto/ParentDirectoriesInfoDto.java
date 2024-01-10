package me.hamtom.thor.directory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 부모 디렉토리 중 누락된 디렉토리 확인
 */
@Data
@AllArgsConstructor
public class ParentDirectoriesInfoDto {
    private String childDirectory;
    private List<String> missingDirectories;
    private List<String> existingDirectories;
    private List<String> orphanedDirectories;
}
