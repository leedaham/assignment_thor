package me.hamtom.thor.directory.domain.getSublist.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetSublistCommand {
    private String pathName;
}
