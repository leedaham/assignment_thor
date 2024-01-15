package me.hamtom.thor.directory.domain.create;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import jakarta.inject.Inject;
import me.hamtom.thor.directory.domain.common.entity.Directory;
import me.hamtom.thor.directory.domain.common.repository.DirectoryRepository;
import me.hamtom.thor.directory.domain.common.response.SuccessResult;
import me.hamtom.thor.directory.domain.create.dto.CreateResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(properties = {"config.policy.create-parents.limit=5"})
class CreateControllerTest {

    @Inject
    private MockMvc mockMvc;
    @Autowired
    private DirectoryRepository directoryRepository;


    @DisplayName("[성공] 디렉토리 추가: 중복X, 가장 윗 계층")
    @Test
    void create1() throws Exception {
        //given
        String reqUrl = "/directory/create";
        String pathName = "/test";
        String owner = "root";
        String group = "rootGroup";
        String permissions = "rwxrwxr--";
        int size = 100;
        CreateController.CreateDirReq createDirReq = new CreateController.CreateDirReq(pathName, owner, group, permissions, size);

        //when
        MvcResult mvcResult = mockMvc.perform(
                        post(reqUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new Gson().toJson(createDirReq)))
                .andDo(print())
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        SuccessResult responseBody = mapper.readValue(mvcResult.getResponse().getContentAsString(), SuccessResult.class);
        CreateResult createResult = mapper.convertValue(responseBody.getData(), CreateResult.class);

        Directory directory = directoryRepository.findByPathName(pathName).get();
        //then
        assertThat(responseBody.getHttpStatus()).isEqualTo(HttpStatus.OK);
        assertThat(directory.getPathName()).isEqualTo(createResult.getCreatedDirectory());
        assertThat(directory.getSize()).isEqualTo(createResult.getSize());
    }

    @DisplayName("[실패] 디렉토리 추가: 중복, 가장 윗 계층")
    @Test
    void create2() throws Exception {
        //given
        String reqUrl = "/directory/create";
        String pathName = "/test";
        String owner = "root";
        String group = "rootGroup";
        String permissions = "rwxrwxr--";
        int size = 100;
        CreateController.CreateDirReq createDirReq = new CreateController.CreateDirReq(pathName, owner, group, permissions, size);

        //when
        directoryRepository.save(Directory.createDirectory(pathName, owner, group, permissions, size));
        MvcResult mvcResult = mockMvc.perform(
                        post(reqUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new Gson().toJson(createDirReq)))
                .andDo(print())
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        SuccessResult responseBody = mapper.readValue(mvcResult.getResponse().getContentAsString(), SuccessResult.class);

        //then
        assertThat(responseBody.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @DisplayName("[성공] 디렉토리 추가: 중복X, 가장 윗 계층X, 부모 존재")
    @Test
    void create3() throws Exception {
        //given
        String reqUrl = "/directory/create";
        String parentName = "/abc";
        String dirName = "def";
        String pathName = parentName + "/" + dirName;
        String owner = "root";
        String group = "rootGroup";
        String permissions = "rwxrwxr--";
        int size = 100;

        CreateController.CreateDirReq createDirReq = new CreateController.CreateDirReq(pathName, owner, group, permissions, size);

        //when
        directoryRepository.save(Directory.createDirectory(parentName, owner, group, permissions, size));
        MvcResult mvcResult = mockMvc.perform(
                        post(reqUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new Gson().toJson(createDirReq)))
                .andDo(print())
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        SuccessResult responseBody = mapper.readValue(mvcResult.getResponse().getContentAsString(), SuccessResult.class);
        CreateResult createResult = mapper.convertValue(responseBody.getData(), CreateResult.class);

        Directory directory = directoryRepository.findByPathName(pathName).get();
        //then
        assertThat(responseBody.getHttpStatus()).isEqualTo(HttpStatus.OK);
        assertThat(directory.getPathName()).isEqualTo(createResult.getCreatedDirectory());
        assertThat(directory.getSize()).isEqualTo(createResult.getSize());
    }

    @DisplayName("[실패] 디렉토리 추가: 중복X, 가장 윗 계층X, 부모 존재X - createMissingParent=false or null")
    @Test
    void create4() throws Exception {
        //given
        String reqUrl = "/directory/create";
        String parentName = "/abc";
        String dirName = "def";
        String pathName = parentName + "/" + dirName;
        String owner = "root";
        String group = "rootGroup";
        String permissions = "rwxrwxr--";
        int size = 100;
        CreateController.CreateDirReq createDirReq = new CreateController.CreateDirReq(pathName, owner, group, permissions, size);

        //when
        MvcResult mvcResult = mockMvc.perform(
                        post(reqUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new Gson().toJson(createDirReq)))
                .andDo(print())
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        SuccessResult responseBody = mapper.readValue(mvcResult.getResponse().getContentAsString(), SuccessResult.class);

        boolean isExist = directoryRepository.isExist(pathName);

        //then
        assertThat(responseBody.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(isExist).isFalse();
    }

    @DisplayName("[성공] 디렉토리 추가: 중복X, 가장 윗 계층X, 부모 존재X - createMissingParent=true")
    @Test
    void create5() throws Exception {
        //given
        String reqUrl = "/directory/create";
        String parentName = "/abc";
        String dirName = "def";
        String pathName = parentName + "/" + dirName;
        String owner = "root";
        String group = "rootGroup";
        String permissions = "rwxrwxr--";
        int size = 100;
        String createMissingParent = "true";
        CreateController.CreateDirReq createDirReq = new CreateController.CreateDirReq(pathName, owner, group, permissions, size);

        //when
        MvcResult mvcResult = mockMvc.perform(
                        post(reqUrl)
                                .param("createMissingParent", createMissingParent)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new Gson().toJson(createDirReq)))
                .andDo(print())
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        SuccessResult responseBody = mapper.readValue(mvcResult.getResponse().getContentAsString(), SuccessResult.class);
        CreateResult createResult = mapper.convertValue(responseBody.getData(), CreateResult.class);

        Directory directory = directoryRepository.findByPathName(pathName).get();
        Directory parentDirectory = directoryRepository.findByPathName(parentName).get();
        //then
        assertThat(responseBody.getHttpStatus()).isEqualTo(HttpStatus.OK);
        assertThat(directory.getPathName()).isEqualTo(createResult.getCreatedDirectory());
        assertThat(directory.getSize()).isEqualTo(createResult.getSize());
        assertThat(parentDirectory.getPathName()).isEqualTo(createResult.getCreatedParentDirectories().get(0));
        assertThat(parentDirectory.getSize()).isEqualTo(createResult.getSize());
    }
    @DisplayName("[실패] 디렉토리 추가: 중복X, 가장 윗 계층X, 부모 존재X, 일정 개수 이상의 폴더 생성 - createMissingParent=true")
    @Test
    void create6() throws Exception {
        //given
        String reqUrl = "/directory/create";
        String parentName = "/abc/abc/abc/abc/abc/a";
        String dirName = "def";
        String pathName = parentName + "/" + dirName;
        String owner = "root";
        String group = "rootGroup";
        String permissions = "rwxrwxr--";
        int size = 100;
        String createMissingParent = "true";
        CreateController.CreateDirReq createDirReq = new CreateController.CreateDirReq(pathName, owner, group, permissions, size);

        //when
        MvcResult mvcResult = mockMvc.perform(
                        post(reqUrl)
                                .param("createMissingParent", createMissingParent)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new Gson().toJson(createDirReq)))
                .andDo(print())
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        SuccessResult responseBody = mapper.readValue(mvcResult.getResponse().getContentAsString(), SuccessResult.class);
        //then
        assertThat(responseBody.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    @DisplayName("[실패] 디렉토리 추가: / 생성")
    @Test
    void create7() throws Exception {
        //given
        String reqUrl = "/directory/create";
        String pathName = "/";
        String owner = "root";
        String group = "rootGroup";
        String permissions = "rwxrwxr--";
        int size = 100;
        String createMissingParent = "true";
        CreateController.CreateDirReq createDirReq = new CreateController.CreateDirReq(pathName, owner, group, permissions, size);

        //when
        MvcResult mvcResult = mockMvc.perform(
                        post(reqUrl)
                                .param("createMissingParent", createMissingParent)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new Gson().toJson(createDirReq)))
                .andDo(print())
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        SuccessResult responseBody = mapper.readValue(mvcResult.getResponse().getContentAsString(), SuccessResult.class);
        //then
        assertThat(responseBody.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

}