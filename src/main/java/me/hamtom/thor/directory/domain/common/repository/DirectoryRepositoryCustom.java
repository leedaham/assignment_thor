package me.hamtom.thor.directory.domain.common.repository;

import java.util.List;

public interface DirectoryRepositoryCustom {
    boolean isExist(String pathName);
    Integer getAllUsedCapacity();

    List<String> getChildDirectoriesPathName(String pathName);

    long updateDirectoryPathName(String oldPathName, String newPathName);

    long deleteDirectoryWithChild(String pathName);

}
