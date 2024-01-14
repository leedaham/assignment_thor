package me.hamtom.thor.directory.domain.create.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateCommand {
    private String pathName;
    private String owner;
    private String group;
    private String permissions;
    private int size;
    private boolean createMissingParent;
    private boolean flexibleCapacity;
}
