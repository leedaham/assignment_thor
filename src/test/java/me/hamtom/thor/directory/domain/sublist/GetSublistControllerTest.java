package me.hamtom.thor.directory.domain.sublist;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.hamtom.thor.directory.domain.common.exception.PredictableRuntimeException;
import me.hamtom.thor.directory.domain.common.repository.DirectoryRepository;
import me.hamtom.thor.directory.domain.common.response.Result;
import me.hamtom.thor.directory.domain.common.response.SuccessResult;
import me.hamtom.thor.directory.domain.sublist.dto.GetSublistResult;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class GetSublistControllerTest {

    @MockBean
    private DirectoryRepository directoryRepository;

    @Autowired
    GetSublistController controller;

    //MockMvc.perform()으로 테스트 환경에서 API 요청시 PathVariable 변수의 %2F 가 한번 더 디코딩되어 %252F로 요청됨.
    //Mock API 요청 대신 Controller method 테스트로 대체

    @DisplayName("[성공] 서브리스트 조회: 존재")
    @Test
    void sublist1() {
        //given
        String pathName = "/test";
        List<String> childDirectoriesPathName
                = new ArrayList<>(Arrays.asList("/test", "/test/abc", "/test/abc/qwe", "/test/abc/asd"));

        //when
        Mockito
                .when(directoryRepository.isExist(pathName))
                .thenReturn(true);
        Mockito
                .when(directoryRepository.getChildDirectoriesPathName(pathName))
                .thenReturn(childDirectoriesPathName);

        ObjectMapper mapper = new ObjectMapper();

        ResponseEntity<Result> responseEntity = controller.getSublistDirectory(pathName);
        SuccessResult responseBody = (SuccessResult) responseEntity.getBody();

        GetSublistResult result = mapper.convertValue(responseBody.getData(), GetSublistResult.class);

        //then
        assertThat(responseBody.getHttpStatus()).isEqualTo(HttpStatus.OK);
        assertThat(result.getName()).isEqualTo(pathName);
        assertThat(result.getSubDirectories()).hasSize(1);
        assertThat(result.getSubDirectories().get(0).getName()).isEqualTo("/test/abc");
        assertThat(result.getSubDirectories().get(0).getSubDirectories()).hasSize(2);
        assertThat(result.getSubDirectories().get(0).getSubDirectories().get(0).getName()).isEqualTo("/test/abc/qwe");
        assertThat(result.getSubDirectories().get(0).getSubDirectories().get(1).getName()).isEqualTo("/test/abc/asd");
    }
    @DisplayName("[실패] 서브리스트 조회: 존재X")
    @Test
    void sublist2() {
        //given
        String pathName = "/test";

        //when
        Mockito
                .when(directoryRepository.isExist(pathName))
                .thenReturn(false);

        //then
        String exceptionMsg = assertThrows(
                PredictableRuntimeException.class,
                () -> controller.getSublistDirectory(pathName)).getMessage();
        System.out.println("exceptionMsg = " + exceptionMsg);
    }
    @DisplayName("[성공] 서브리스트 조회: / 조회, 전체 조회")
    @Test
    void sublist3() {
        //given
        String pathName = "/";
        List<String> childDirectoriesPathName
                = new ArrayList<>(Arrays.asList("/test", "/test/abc", "/test/abc/qwe", "/test/abc/asd"));

        //when
        Mockito
                .when(directoryRepository.isExist(pathName))
                .thenReturn(true);
        Mockito
                .when(directoryRepository.getChildDirectoriesPathName(pathName))
                .thenReturn(childDirectoriesPathName);

        ObjectMapper mapper = new ObjectMapper();

        ResponseEntity<Result> responseEntity = controller.getSublistDirectory(pathName);
        SuccessResult responseBody = (SuccessResult) responseEntity.getBody();

        GetSublistResult result = mapper.convertValue(responseBody.getData(), GetSublistResult.class);

        //then
        assertThat(responseBody.getHttpStatus()).isEqualTo(HttpStatus.OK);
        assertThat(result.getName()).isEqualTo(pathName);
        assertThat(result.getSubDirectories()).hasSize(1);
        assertThat(result.getSubDirectories().get(0).getName()).isEqualTo("/test");
        assertThat(result.getSubDirectories().get(0).getSubDirectories()).hasSize(1);
        assertThat(result.getSubDirectories().get(0).getSubDirectories().get(0).getName()).isEqualTo("/test/abc");
        assertThat(result.getSubDirectories().get(0).getSubDirectories().get(0).getSubDirectories()).hasSize(2);
        assertThat(result.getSubDirectories().get(0).getSubDirectories().get(0).getSubDirectories().get(0).getName()).isEqualTo("/test/abc/qwe");
        assertThat(result.getSubDirectories().get(0).getSubDirectories().get(0).getSubDirectories().get(1).getName()).isEqualTo("/test/abc/asd");
    }
}