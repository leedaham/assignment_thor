package me.hamtom.thor.directory.domain.sublist.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetSublistCommand {
    private String pathName;
}
