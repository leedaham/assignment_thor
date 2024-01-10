package me.hamtom.thor.directory.repository;

import me.hamtom.thor.directory.entity.Directory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface DirectoryRepository extends JpaRepository<Directory, Long> , DirectoryRepositoryCustom {
    Optional<Directory> findByPathName(String pathName);
    int countByPathName(String pathName);

    void deleteByPathName(String pathName);
}
