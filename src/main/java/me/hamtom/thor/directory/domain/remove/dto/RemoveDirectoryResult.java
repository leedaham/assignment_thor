package me.hamtom.thor.directory.domain.remove.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RemoveDirectoryResult {
    private String removedDirectory;
    private List<String> removeChildDirectories;
}
