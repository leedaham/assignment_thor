package me.hamtom.thor.directory.domain.sublist.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class GetSublistResult {
    private String name;
    private List<GetSublistResult> subDirectories;

    public void addSubDirectory(GetSublistResult subDirectory) {
        this.subDirectories.add(subDirectory);
    }

    public GetSublistResult(String name) {
        this.name = name;
        this.subDirectories = new ArrayList<>();
    }
}
