package me.hamtom.thor.directory.domain.remove.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RemoveCommand {
    private String pathName;
    private boolean removeWithChild;
}
