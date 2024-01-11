package me.hamtom.thor.directory.domain.common.directory.repository;

import java.util.List;

public interface DirectoryRepositoryCustom {
    Integer getAllUsedCapacity();

    List<String> getChildDirectoriesPathName(String pathName);

    long renameDirectory(String oldPathName, String newPathName);
}
