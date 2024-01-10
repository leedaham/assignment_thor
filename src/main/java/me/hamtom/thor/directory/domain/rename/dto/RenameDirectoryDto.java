package me.hamtom.thor.directory.domain.rename.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.hamtom.thor.directory.domain.common.enumerated.OptionValue;

@Data
@AllArgsConstructor
public class RenameDirectoryDto {
    private String oldPathName;
    private String newName;
    private OptionValue mergeOnDuplicate;
}
