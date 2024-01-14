package me.hamtom.thor.directory.domain.info.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetInfoResult {
    private String pathName;
    private String owner;
    private String group;
    private String permissions;
    private int size;
    private String created;
    private String modified;
}
