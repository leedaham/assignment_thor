package me.hamtom.thor.directory.domain.getSublist.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
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
