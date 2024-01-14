package me.hamtom.thor.directory.domain.info.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetInfoCommand {
    private String pathName;
}
