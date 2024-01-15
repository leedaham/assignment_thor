package me.hamtom.thor.directory.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import me.hamtom.thor.directory.domain.common.DirectoryService;
import me.hamtom.thor.directory.domain.common.dto.ChildDirectoriesInfoDto;
import me.hamtom.thor.directory.domain.common.dto.ParentDirectoriesInfoDto;
import me.hamtom.thor.directory.domain.common.entity.Directory;
import me.hamtom.thor.directory.domain.common.exception.PredictableRuntimeException;
import me.hamtom.thor.directory.domain.common.repository.DirectoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DirectoryServiceTest {
    @PersistenceContext
    EntityManager em;
    @Autowired
    DirectoryService directoryService;
    @Autowired
    DirectoryRepository directoryRepository;

    private final String owner = "root";
    private final String group = "rootGroup";
    private final String permissions = "rwxrwxrwx";
    private final int size = 20;

    @DisplayName("디렉토리 저장")
    @Test
    void service1(){
        //given
        String pathName = "/test";

        //when
        directoryService.saveDirectory(pathName, owner, group, permissions, size);

        //then
        Directory directory = directoryRepository.findByPathName(pathName).get();
        assertThat(directory.getPathName()).isEqualTo(pathName);
    }
    @DisplayName("디렉토리 저장 (여러개)")
    @Test
    void service2(){
        //given
        List<String> pathNames = List.of("/test", "/abc", "/def");

        //when
        directoryService.saveDirectories(pathNames, owner, group, permissions, size);

        //then
        List<String> directories = new ArrayList<>();
        for (String pathName : pathNames) {
            Directory directory = directoryRepository.findByPathName(pathName).get();
            directories.add(directory.getPathName());
        }
        assertThat(directories).hasSize(3);
    }
    @DisplayName("디렉토리 경로 변경")
    @Test
    void service3(){
        //given
        String pathName = "/test";
        String toMigratePath = "/new";
        Map<String, String> migrationMap = new HashMap<>();
        migrationMap.put(toMigratePath, pathName);

        //when
        directoryRepository.save(Directory.createDirectory(pathName, owner, group, permissions, size));
        directoryService.updateDirectoriesPathName(migrationMap);
        em.clear();

        //then
        Directory directory = directoryRepository.findByPathName(toMigratePath).get();
        boolean isOldPathExist = directoryRepository.isExist(pathName);
        assertThat(directory.getPathName()).isEqualTo(toMigratePath);
        assertThat(isOldPathExist).isFalse();
    }
    @DisplayName("디렉토리 삭제")
    @Test
    void service4(){
        //given
        String pathName = "/test";

        //when
        directoryRepository.save(Directory.createDirectory(pathName, owner, group, permissions, size));
        directoryService.deleteDirectory(pathName);

        //then
        boolean isPathExist = directoryRepository.isExist(pathName);
        assertThat(isPathExist).isFalse();
    }
    @DisplayName("디렉토리 삭제 (자식 포함)")
    @Test
    void service5(){
        //given
        String pathName = "/test";
        String childName = "/test/son";

        //when
        directoryRepository.save(Directory.createDirectory(pathName, owner, group, permissions, size));
        directoryRepository.save(Directory.createDirectory(childName, owner, group, permissions, size));
        directoryService.deleteDirectoryWithChild(pathName);

        //then
        boolean isPathExist = directoryRepository.isExist(pathName);
        boolean isChildPathExist = directoryRepository.isExist(childName);
        assertThat(isPathExist).isFalse();
        assertThat(isChildPathExist).isFalse();
    }

    @DisplayName("디렉토리 존재 조회")
    @Test
    void service6(){
        //given
        String pathName = "/test";

        //when
        directoryRepository.save(Directory.createDirectory(pathName, owner, group, permissions, size));
        boolean directoryExist = directoryService.isDirectoryExist(pathName);

        //then
        assertThat(directoryExist).isTrue();
    }
    @DisplayName("디렉토리 존재 조회 (경로가 비어있는지 확인)")
    @Test
    void service7(){
        //given
        String pathName = "/test";

        //when
        directoryRepository.save(Directory.createDirectory(pathName, owner, group, permissions, size));

        //then
        String exceptionMsg = assertThrows(
                PredictableRuntimeException.class,
                () -> directoryService.checkAvailablePathName(pathName)).getMessage();
        System.out.println("exceptionMsg = " + exceptionMsg);
    }
    @DisplayName("디렉토리 존재 조회 (경로가 사용중인지 확인)")
    @Test
    void service8(){
        //given
        String pathName = "/test";

        //when
        //then
        String exceptionMsg = assertThrows(
                PredictableRuntimeException.class,
                () -> directoryService.checkExistPathName(pathName)).getMessage();
        System.out.println("exceptionMsg = " + exceptionMsg);
    }
    @DisplayName("디렉토리 조회")
    @Test
    void service9(){
        //given
        String pathName = "/test";

        //when
        directoryRepository.save(Directory.createDirectory(pathName, owner, group, permissions, size));
        Directory directory = directoryService.checkExistAndGetDirectory(pathName);

        //then
        assertThat(directory.getPathName()).isEqualTo(pathName);
    }
    @DisplayName("디렉토리 부모 정보 조회")
    @Test
    void service10(){
        //given
        String pathName = "/test/abc/qwer";

        //when
        directoryRepository.save(Directory.createDirectory(pathName, owner, group, permissions, size));
        directoryRepository.save(Directory.createDirectory("/test", owner, group, permissions, size));
        ParentDirectoriesInfoDto parentDirectoriesInfo = directoryService.getParentDirectoriesInfo(pathName);

        //then
        assertThat(parentDirectoriesInfo.getPathName()).isEqualTo(pathName);
        assertThat(parentDirectoriesInfo.getMissingDirectories()).containsExactly("/test/abc");
        assertThat(parentDirectoriesInfo.getExistingDirectories()).containsExactly("/test");
    }
    @DisplayName("디렉토리 자식 정보 조회")
    @Test
    void service11(){
        //given
        String pathName = "/test";
        String childName1 = pathName + "/abc";
        String childName2 = pathName + "/asd";
        //when
        directoryRepository.save(Directory.createDirectory(pathName, owner, group, permissions, size));
        directoryRepository.save(Directory.createDirectory(childName1, owner, group, permissions, size));
        directoryRepository.save(Directory.createDirectory(childName2, owner, group, permissions, size));
        ChildDirectoriesInfoDto childDirectoriesInfo = directoryService.getChildDirectoriesInfo(pathName);

        //then
        assertThat(childDirectoriesInfo.getPathName()).isEqualTo(pathName);
        assertThat(childDirectoriesInfo.getChildDirectories()).containsExactlyInAnyOrder(childName1, childName2);
    }

    @DisplayName("현재 디렉토리들이 사용중인 Size 구하기")
    @Test
    void service12(){
        //given
        String pathName = "/test";
        String childName1 = pathName + "/abc";
        String childName2 = pathName + "/asd";

        //when
        directoryRepository.save(Directory.createDirectory(pathName, owner, group, permissions, size));
        directoryRepository.save(Directory.createDirectory(childName1, owner, group, permissions, size));
        directoryRepository.save(Directory.createDirectory(childName2, owner, group, permissions, size));
        int usedCapacity = directoryService.getUsedCapacity();

        //then
        assertThat(usedCapacity).isEqualTo(size * 3);
    }

    @DisplayName("디렉토리 계층 구하기")
    @Test
    void service13(){
        //given
        String pathName = "/test";
        String childName1 = pathName + "/abc";
        String childName2 = pathName + "/asd/ade";

        //when
        int layerNum = directoryService.getLayerNum(pathName);
        int childLayerNum1 = directoryService.getLayerNum(childName1);
        int childLayerNum2 = directoryService.getLayerNum(childName2);

        //then
        assertThat(layerNum).isEqualTo(1);
        assertThat(childLayerNum1).isEqualTo(2);
        assertThat(childLayerNum2).isEqualTo(3);
    }

    @DisplayName("경로 root 인지 확인")
    @Test
    void service14(){
        //given
        String pathName1 = "/abc";
        String pathName2 = "/";

        //when
        boolean rootPath1 = directoryService.isRootPath(pathName1);
        boolean rootPath2 = directoryService.isRootPath(pathName2);

        //then
        assertThat(rootPath1).isFalse();
        assertThat(rootPath2).isTrue();
    }
    @DisplayName("경로 root 이면 예외 발생")
    @Test
    void service15(){
        //given
        String pathName1 = "/abc";
        String pathName2 = "/";

        //when
        //then
        directoryService.throwExceptionIfRootPath(pathName1);
        String exceptionMsg = assertThrows(
                PredictableRuntimeException.class,
                () -> directoryService.throwExceptionIfRootPath(pathName2)).getMessage();
        System.out.println("exceptionMsg = " + exceptionMsg);
    }
}