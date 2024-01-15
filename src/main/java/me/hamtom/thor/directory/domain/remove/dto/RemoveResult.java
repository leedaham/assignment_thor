package me.hamtom.thor.directory.domain.remove.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RemoveResult {
    private String removedDirectory;
    private List<String> removeChildDirectories;
}
