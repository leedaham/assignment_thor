package me.hamtom.thor.directory.domain.migrate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class MergeInfoDto {
    private Map<String, String> migrationMap;
    private List<String> mergedList;
}
