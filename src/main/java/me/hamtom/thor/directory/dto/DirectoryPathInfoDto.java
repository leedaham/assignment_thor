package me.hamtom.thor.directory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DirectoryPathInfoDto {
    String pathName;
    private int layerCount;
    private String dirName;
    private List<String> layers = new ArrayList<>();
}
