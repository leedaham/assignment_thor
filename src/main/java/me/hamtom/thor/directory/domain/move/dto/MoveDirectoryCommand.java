package me.hamtom.thor.directory.domain.move.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.hamtom.thor.directory.domain.common.enumerated.OptionValue;

@Data
@AllArgsConstructor
public class MoveDirectoryCommand {
    private String sourcePath;
    private String targetPath;
    private OptionValue moveWithChild;
    private OptionValue mergeOnDuplicate;
}
