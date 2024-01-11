package me.hamtom.thor.directory.domain.create.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CreateDirectoryResultDto {
    private String createdDirectory;
    private List<String> createdParentDirectories;
    private int size;
}
