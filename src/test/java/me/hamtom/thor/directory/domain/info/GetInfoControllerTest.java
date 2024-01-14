package me.hamtom.thor.directory.domain.info;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import jakarta.inject.Inject;
import me.hamtom.thor.directory.domain.common.DirectoryService;
import me.hamtom.thor.directory.domain.common.entity.Directory;
import me.hamtom.thor.directory.domain.common.repository.DirectoryRepository;
import me.hamtom.thor.directory.domain.common.response.Result;
import me.hamtom.thor.directory.domain.common.response.SuccessResult;
import me.hamtom.thor.directory.domain.info.dto.GetInfoResult;
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

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class GetInfoControllerTest {

    @Inject
    private MockMvc mockMvc;
    @MockBean
    private DirectoryService directoryService;

    @Autowired
    private GetInfoController controller;

    @Test
    void test1() throws Exception {
        //given


        String pathName = "/test";
        String owner = "root";
        String group = "rootGroup";
        String permissions = "rwxrwxr--";

        int size = 100;
        Directory directory = Directory.createDirectory(pathName, owner, group, permissions, size);
        directory.setCreated(LocalDateTime.now());
        directory.setModified(LocalDateTime.now());
        Mockito
                .when(directoryService.checkExistAndGetDirectory(pathName))
                .thenReturn(directory);

        ObjectMapper mapper = new ObjectMapper();

        ResponseEntity<Result> responseEntity = controller.getDirectoryInfo(pathName);
        SuccessResult responseBody = (SuccessResult) responseEntity.getBody();

//        String reqUrl = "/directory/info/{pathName}";
//        String pathVariablePathName = "%2Ftest";
        //when
//        MvcResult mvcResult = mockMvc.perform(
//                        MockMvcRequestBuilders.get(new URI(reqUrl, pathVariablePathName)))
//                .andDo(print())
//                .andReturn();
//
//        ObjectMapper mapper = new ObjectMapper();
//        SuccessResult responseBody = mapper.readValue(mvcResult.getResponse().getContentAsString(), SuccessResult.class);
        GetInfoResult result = mapper.convertValue(responseBody.getData(), GetInfoResult.class);

        //then
        assertThat(responseBody.getHttpStatus()).isEqualTo(HttpStatus.OK);
        assertThat(result.getPathName()).isEqualTo(pathName);
        assertThat(result.getOwner()).isEqualTo(owner);
        assertThat(result.getGroup()).isEqualTo(group);
        assertThat(result.getPermissions()).isEqualTo(permissions);
        assertThat(result.getSize()).isEqualTo(size);
    }
}