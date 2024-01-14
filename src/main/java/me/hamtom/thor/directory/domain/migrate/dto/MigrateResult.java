package me.hamtom.thor.directory.domain.migrate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.hamtom.thor.directory.domain.migrate.enumerated.MergeStatus;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MigrateResult {
    private String sourcePath;
    private String toMigratePath;
    private MergeStatus mergeStatus;

    private List<String> migratedChildDirectories;
    private List<String> mergedChildDirectories;

    public MigrateResult(String sourcePath, String toMigratePath, MergeStatus mergeStatus) {
        this.sourcePath = sourcePath;
        this.toMigratePath = toMigratePath;
        this.mergeStatus = mergeStatus;
    }
}
