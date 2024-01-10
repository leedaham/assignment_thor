package me.hamtom.thor.directory.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateDirectoryResultDto {
    private String pathName;
    private List<String> createdParentDirectories;
}
