package me.hamtom.thor.directory.domain.common.repository;

import me.hamtom.thor.directory.domain.common.entity.Directory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface DirectoryRepository extends JpaRepository<Directory, Long> , DirectoryRepositoryCustom {
    Optional<Directory> findByPathName(String pathName);
    void deleteByPathName(String pathName);
}
