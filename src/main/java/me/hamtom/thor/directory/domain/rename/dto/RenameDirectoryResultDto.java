package me.hamtom.thor.directory.domain.rename.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.hamtom.thor.directory.domain.rename.enumerated.RenameType;

import java.util.List;

@Data
@AllArgsConstructor
public class RenameDirectoryResultDto {
    private RenameType renameOrMerge;
    private String oldPathName;
    private String newPathName;
    private List<String> renamedChildDirectories;
    private List<String> mergedChildDirectories;

}
