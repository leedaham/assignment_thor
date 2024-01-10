package me.hamtom.thor.directory.dto;

import lombok.Data;

import java.util.List;

@Data
public class SublistDto {
    private String name;
    private List<SublistDto> subDirectories;
}
