package me.hamtom.thor.directory.domain.create.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.hamtom.thor.directory.domain.common.enumerated.OptionValue;

@Data
@AllArgsConstructor
public class CreateDirectoryCommand {
    private String pathName;
    private String owner;
    private String group;
    private String permissions;
    private int size;
    private OptionValue createMissingParent;
    private OptionValue flexibleCapacity;
}
