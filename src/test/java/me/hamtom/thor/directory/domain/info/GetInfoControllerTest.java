package me.hamtom.thor.directory.domain.info;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.hamtom.thor.directory.domain.common.entity.Directory;
import me.hamtom.thor.directory.domain.common.exception.PredictableRuntimeException;
import me.hamtom.thor.directory.domain.common.repository.DirectoryRepository;
import me.hamtom.thor.directory.domain.common.response.Result;
import me.hamtom.thor.directory.domain.common.response.SuccessResult;
import me.hamtom.thor.directory.domain.info.dto.GetInfoResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class GetInfoControllerTest {

    @MockBean
    private DirectoryRepository directoryRepository;

    @Autowired
    private GetInfoController controller;

    //MockMvc.perform()으로 테스트 환경에서 API 요청시 PathVariable 변수의 %2F 가 한번 더 디코딩되어 %252F로 요청됨.
    //Mock API 요청 대신 Controller method 테스트로 대체

    @DisplayName("[성공] 디렉토리 조회: 존재")
    @Test
    void getInfo1() {
        //given
        String pathName = "/test";
        String owner = "root";
        String group = "rootGroup";
        String permissions = "rwxrwxr--";
        int size = 100;

        //when
        Directory directory = Directory.createDirectory(pathName, owner, group, permissions, size);
        directory.setCreated(LocalDateTime.now());
        directory.setModified(LocalDateTime.now());
        Mockito
                .when(directoryRepository.findByPathName(pathName))
                .thenReturn(Optional.of(directory));

        ResponseEntity<Result> responseEntity = controller.getDirectoryInfo(pathName);
        SuccessResult responseBody = (SuccessResult) responseEntity.getBody();

        ObjectMapper mapper = new ObjectMapper();
        GetInfoResult result = mapper.convertValue(responseBody.getData(), GetInfoResult.class);

        //then
        assertThat(responseBody.getHttpStatus()).isEqualTo(HttpStatus.OK);
        assertThat(result.getPathName()).isEqualTo(pathName);
        assertThat(result.getOwner()).isEqualTo(owner);
        assertThat(result.getGroup()).isEqualTo(group);
        assertThat(result.getPermissions()).isEqualTo(permissions);
        assertThat(result.getSize()).isEqualTo(size);
    }
    @DisplayName("[실패] 디렉토리 조회: 존재X")
    @Test
    void getInfo2() {
        //given
        String pathName = "/test";

        //when
        Optional<Directory> optionalDirectory = Optional.empty();
        Mockito
                .when(directoryRepository.findByPathName(pathName))
                .thenReturn(optionalDirectory);

        //then
        String exceptionMsg = assertThrows(
                PredictableRuntimeException.class,
                () -> controller.getDirectoryInfo(pathName)).getMessage();
        System.out.println("exceptionMsg = " + exceptionMsg);
    }
    @DisplayName("[실패] 디렉토리 조회: / 조회")
    @Test
    void getInfo3() {
        //given
        String pathName = "/";

        //when
        //then
        String exceptionMsg = assertThrows(
                PredictableRuntimeException.class,
                () -> controller.getDirectoryInfo(pathName)).getMessage();
        System.out.println("exceptionMsg = " + exceptionMsg);
    }
}