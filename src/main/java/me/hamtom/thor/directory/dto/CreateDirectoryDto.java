package me.hamtom.thor.directory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.hamtom.thor.directory.dto.enumerated.OptionValue;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDirectoryDto {
    private String pathName;
    private String owner;
    private String group;
    private String permissions;
    private int size;
    private OptionValue createMissingParent;
    private OptionValue flexibleCapacity;

}
