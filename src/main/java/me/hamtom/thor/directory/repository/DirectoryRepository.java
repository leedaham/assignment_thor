package me.hamtom.thor.directory.repository;

import me.hamtom.thor.directory.entity.Directory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DirectoryRepository extends JpaRepository<Directory, Long> {
}
