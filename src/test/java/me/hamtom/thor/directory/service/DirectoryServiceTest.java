package me.hamtom.thor.directory.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import me.hamtom.thor.directory.dto.DirectoryPathInfoDto;
import me.hamtom.thor.directory.dto.ParentDirectoriesInfoDto;
import me.hamtom.thor.directory.entity.Directory;
import me.hamtom.thor.directory.repository.DirectoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
class DirectoryServiceTest {

    @PersistenceContext
    EntityManager em;
    @Autowired
    DirectoryService directoryService;
    @Autowired
    DirectoryRepository directoryRepository;

    @DisplayName("디렉토리 존재 확인")
    @Test
    void isDirectoryExist(){
        //given
        String pathName1 = "/aaa/bbb/ccc";
        String pathName2 = "/aaa/bbb/ddd";
        String owner = "root";
        String group = "rootGroup";
        String permissions = "rwxrwxrwx";
        int size = 100;

        //when
        Directory directory = Directory.createDirectory(pathName1, owner, group, permissions, size);
        directoryRepository.save(directory);

        //then
        boolean directoryExist1 = directoryService.isDirectoryExist(pathName1);
        boolean directoryExist2 = directoryService.isDirectoryExist(pathName2);
        assertThat(directoryExist1).isTrue();
        assertThat(directoryExist2).isFalse();
    }

    @DisplayName("디렉토리 계층 정보 구하기")
    @ParameterizedTest
    @CsvSource(value = {
            "/abc/sde/sda/ewq, 4, ewq",
            "/abc/sda/ewz, 3, ewz",
            "/abc/sde, 2, sde",
            "/abc, 1, abc"
    })
    void getDirectoryPathInfo( String pathName, int layerCount, String dirName ){
        //when
        DirectoryPathInfoDto directoryPathInfo = directoryService.getDirectoryPathInfo(pathName);
//        System.out.println(directoryPathInfo.toString());

        //then
        assertThat(directoryPathInfo.getPathName()).isEqualTo(pathName);
        assertThat(directoryPathInfo.getLayerCount()).isEqualTo(layerCount);
        assertThat(directoryPathInfo.getDirName()).isEqualTo(dirName);
        assertThat(directoryPathInfo.getLayers()).hasSize(layerCount);
    }

    @DisplayName("부모 디렉토리 정보 구하기")
    @Test
    void getParentDirectoriesInfo(){
        //given
        String parentPathName1 = "/aaa";
        String parentPathName2 = "/aaa/bbb";
        String orphanedPathName = "/aaa/bbb/ccc/ddd";
        String owner = "root";
        String group = "rootGroup";
        String permissions = "rwxrwxrwx";
        int size = 100;

        //when
        Directory parentDirectory1 = Directory.createDirectory(parentPathName1, owner, group, permissions, size);
        Directory parentDirectory2 = Directory.createDirectory(parentPathName2, owner, group, permissions, size);
        Directory orphanedDirectory = Directory.createDirectory(orphanedPathName, owner, group, permissions, size);
        directoryRepository.save(parentDirectory1);
        directoryRepository.save(parentDirectory2);
        directoryRepository.save(orphanedDirectory);

        String pathName = "/aaa/bbb/ccc/ddd/eee";
        List<String> layers = new ArrayList<>();
        layers.add("aaa");
        layers.add("bbb");
        layers.add("ccc");
        layers.add("ddd");
        layers.add("eee");

        ParentDirectoriesInfoDto parentDirectoriesInfoDto = directoryService.getParentDirectoriesInfo(pathName, layers);

        //then
        assertThat(parentDirectoriesInfoDto.getChildDirectory()).isEqualTo(pathName);
        assertThat(parentDirectoriesInfoDto.getMissingDirectories()).contains("/aaa/bbb/ccc");
        assertThat(parentDirectoriesInfoDto.getExistingDirectories()).contains("/aaa", "/aaa/bbb");
        assertThat(parentDirectoriesInfoDto.getOrphanedDirectories()).contains("/aaa/bbb/ccc/ddd");
    }

    @DisplayName("고아 디렉토리 삭제하기")
    @Test
    void removeOrphanedDirectory(){
        //given
        String parentPathName1 = "/aaa";
        String parentPathName2 = "/aaa/bbb";
        String orphanedPathName = "/aaa/bbb/ccc/ddd";
        String owner = "root";
        String group = "rootGroup";
        String permissions = "rwxrwxrwx";
        int size = 100;

        //when
        Directory parentDirectory1 = Directory.createDirectory(parentPathName1, owner, group, permissions, size);
        Directory parentDirectory2 = Directory.createDirectory(parentPathName2, owner, group, permissions, size);
        Directory orphanedDirectory = Directory.createDirectory(orphanedPathName, owner, group, permissions, size);
        directoryRepository.save(parentDirectory1);
        directoryRepository.save(parentDirectory2);
        directoryRepository.save(orphanedDirectory);

        String pathName = "/aaa/bbb/ccc/ddd/eee";
        List<String> layers = new ArrayList<>();
        layers.add("aaa");
        layers.add("bbb");
        layers.add("ccc");
        layers.add("ddd");
        layers.add("eee");

        ParentDirectoriesInfoDto parentDirectoriesInfoDto = directoryService.getParentDirectoriesInfo(pathName, layers);
        directoryService.removeOrphanedDirectory(parentDirectoriesInfoDto.getOrphanedDirectories());
        int countByPathName = directoryRepository.countByPathName(pathName);

        //then
        assertThat(countByPathName).isZero();
    }
}