package me.hamtom.thor.directory.domain.sublist;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import me.hamtom.thor.directory.domain.common.DirectoryService;
import me.hamtom.thor.directory.domain.common.entity.Directory;
import me.hamtom.thor.directory.domain.common.exception.PredictableRuntimeException;
import me.hamtom.thor.directory.domain.common.repository.DirectoryRepository;
import me.hamtom.thor.directory.domain.common.response.FailResult;
import me.hamtom.thor.directory.domain.common.response.Result;
import me.hamtom.thor.directory.domain.common.response.SuccessResult;
import me.hamtom.thor.directory.domain.info.dto.GetInfoResult;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class GetSublistControllerTest {

    @Inject
    private MockMvc mockMvc;

    @Autowired
    private DirectoryService directoryService;

    @MockBean
    private DirectoryRepository directoryRepository;

    @Autowired
    GetSublistController controller;

    @DisplayName("[성공] 서브리스트 조회")
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
        assertThrows(
                PredictableRuntimeException.class,
                () -> controller.getSublistDirectory(pathName));
    }
}