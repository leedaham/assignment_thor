package me.hamtom.thor.directory.domain.common.directory.dto;

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
}
