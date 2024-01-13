package me.hamtom.thor.directory.domain.getInfo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetDirectoryInfoResultDto {
    private String pathName;
    private String owner;
    private String group;
    private String permissions;
    private int size;
    private String created;
    private String modified;
}
