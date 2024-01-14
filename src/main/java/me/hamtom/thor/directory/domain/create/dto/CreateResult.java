package me.hamtom.thor.directory.domain.create.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateResult {
    private String createdDirectory;
    private List<String> createdParentDirectories;
    private int size;
}
