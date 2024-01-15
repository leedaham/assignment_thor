package me.hamtom.thor.directory.domain.remove;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import me.hamtom.thor.directory.domain.common.DirectoryService;
import me.hamtom.thor.directory.domain.common.exception.PredictableRuntimeException;
import me.hamtom.thor.directory.domain.common.response.Result;
import me.hamtom.thor.directory.domain.common.response.SuccessResult;
import me.hamtom.thor.directory.domain.create.CreateService;
import me.hamtom.thor.directory.domain.create.dto.CreateCommand;
import me.hamtom.thor.directory.domain.remove.dto.RemoveResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RemoveControllerTest {

    @PersistenceContext
    EntityManager em;
    @Autowired
    DirectoryService directoryService;
    @Autowired
    CreateService createService;
    @Autowired
    RemoveController controller;

    private final String owner = "root";
    private final String group = "rootGroup";
    private final String permissions = "rwxrwxrwx";
    private final int size = 100;


    //MockMvc.perform()으로 테스트 환경에서 API 요청시 PathVariable 변수의 %2F 가 한번 더 디코딩되어 %252F로 요청됨.
    //Mock API 요청 대신 Controller method 테스트로 대체

    @DisplayName("[실패] 디렉토리 삭제: 존재X")
    @Test
    void remove1(){
        //given
        String pathName = "/test";
        String removeWithChild = "false";

        //when
        //then
        String exceptionMsg = assertThrows(
                PredictableRuntimeException.class,
                () -> controller.deleteDirectory(pathName, removeWithChild)).getMessage();
        System.out.println("exceptionMsg = " + exceptionMsg);
    }
    @DisplayName("[실패] 디렉토리 삭제: / 삭제")
    @Test
    void remove2(){
        //given
        String pathName = "/";
        String removeWithChild = "false";

        //when
        //then
        String exceptionMsg = assertThrows(
                PredictableRuntimeException.class,
                () -> controller.deleteDirectory(pathName, removeWithChild)).getMessage();
        System.out.println("exceptionMsg = " + exceptionMsg);
    }

    @DisplayName("[성공] 디렉토리 삭제: 존재, 자식X")
    @Test
    void remove3(){
        //given
        String pathName = "/abc";
        String removeWithChild = "false";

        //when
        createService.createDirectory(new CreateCommand(pathName, owner, group, permissions, size, true, true));
        ResponseEntity<Result> responseEntity = controller.deleteDirectory(pathName, removeWithChild);
        em.flush();
        SuccessResult responseBody = (SuccessResult) responseEntity.getBody();
        ObjectMapper mapper = new ObjectMapper();
        RemoveResult result = mapper.convertValue(responseBody.getData(), RemoveResult.class);

        //then
        assertThat(responseBody.getHttpStatus()).isEqualTo(HttpStatus.OK);
        assertThat(result.getRemovedDirectory()).isEqualTo(pathName);
    }

    @DisplayName("[실패] 디렉토리 삭제: 존재, 자식 존재 - removeWithChild=false")
    @Test
    void remove4(){
        //given
        String pathName = "/abc";
        String removeWithChild = "false";

        //when
        createService.createDirectory(new CreateCommand("/abc/def/qwer", owner, group, permissions, size, true, true));

        //then
        String exceptionMsg = assertThrows(
                PredictableRuntimeException.class,
                () -> controller.deleteDirectory(pathName, removeWithChild)).getMessage();
        System.out.println("exceptionMsg = " + exceptionMsg);
    }
    @DisplayName("[성공] 디렉토리 삭제: 존재, 자식 존재 - removeWithChild=true")
    @Test
    void remove5(){
        //given
        String pathName = "/abc";
        String removeWithChild = "true";

        //when
        createService.createDirectory(new CreateCommand("/abc/def/qwer", owner, group, permissions, size, true, true));
        ResponseEntity<Result> responseEntity = controller.deleteDirectory(pathName, removeWithChild);
        em.flush();
        SuccessResult responseBody = (SuccessResult) responseEntity.getBody();
        ObjectMapper mapper = new ObjectMapper();
        RemoveResult result = mapper.convertValue(responseBody.getData(), RemoveResult.class);

        //then
        assertThat(responseBody.getHttpStatus()).isEqualTo(HttpStatus.OK);
        assertThat(result.getRemovedDirectory()).isEqualTo(pathName);
        assertThat(result.getRemoveChildDirectories()).contains("/abc/def");
        assertThat(result.getRemoveChildDirectories()).contains("/abc/def/qwer");
    }
}