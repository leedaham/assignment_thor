package me.hamtom.thor.directory.domain.migrate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MigrateCommand {
    private String sourcePath;
    private String toMigratePath;
    private boolean mergeOnDuplicate;
}
