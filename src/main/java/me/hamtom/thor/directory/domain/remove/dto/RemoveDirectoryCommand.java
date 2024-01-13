package me.hamtom.thor.directory.domain.remove.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.hamtom.thor.directory.domain.common.enumerated.OptionValue;

@Data
@AllArgsConstructor
public class RemoveDirectoryCommand {
    private String pathName;
    private OptionValue removeWithChild;
}
