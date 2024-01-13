package me.hamtom.thor.directory.domain.move.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.hamtom.thor.directory.domain.move.dto.enumerated.MoveType;

import java.util.List;

@Data
@AllArgsConstructor
public class MoveDirectoryResult {
    private MoveType moveOrMerge;
    private String sourcePath;
    private String targetPath;
    private String toMovePath;
    private List<String> movedChildDirectories;
    private List<String> mergedChildDirectories;

}
