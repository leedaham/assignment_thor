package me.hamtom.thor.directory.domain.common.directory.repository;

import java.util.List;

public interface DirectoryRepositoryCustom {
    boolean isExist(String pathName);
    Integer getAllUsedCapacity();

    List<String> getChildDirectoriesPathName(String pathName);

    long renameDirectory(String oldPathName, String newPathName);

    long deleteDirectoryWithChild(String pathName);

}
