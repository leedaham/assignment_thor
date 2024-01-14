package me.hamtom.thor.directory.domain.create;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import jakarta.inject.Inject;
import me.hamtom.thor.directory.domain.common.entity.Directory;
import me.hamtom.thor.directory.domain.common.repository.DirectoryRepository;
import me.hamtom.thor.directory.domain.common.response.SuccessResult;
import me.hamtom.thor.directory.domain.create.dto.CreateResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(properties = {"config.capacity=10"})
class CreateControllerCapacityTest {

    @Inject
    private MockMvc mockMvc;
    @Autowired
    private DirectoryRepository directoryRepository;


    @DisplayName("[실패] 디렉토리 추가: 중복X, 가장 윗 계층, 용량 부족, flexibleCapacity=false")
    @Test
    void create7() throws Exception {
        //given
        String reqUrl = "/directory/create";
        String pathName = "/test";
        String owner = "root";
        String group = "rootGroup";
        String permissions = "rwxrwxr--";
        String flexibleCapacity="false";
        int size = 100;
        CreateController.CreateDirReq createDirReq = new CreateController.CreateDirReq(pathName, owner, group, permissions, size);

        //when
        MvcResult mvcResult = mockMvc.perform(
                        post(reqUrl)
                                .param("flexibleCapacity", flexibleCapacity)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new Gson().toJson(createDirReq)))
                .andDo(print())
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        SuccessResult responseBody = mapper.readValue(mvcResult.getResponse().getContentAsString(), SuccessResult.class);

        //then
        assertThat(responseBody.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    @DisplayName("[성공] 디렉토리 추가: 중복X, 가장 윗 계층, 용량 부족, flexibleCapacity=true")
    @Test
    void create8() throws Exception {
        //given
        String reqUrl = "/directory/create";
        String pathName = "/test";
        String owner = "root";
        String group = "rootGroup";
        String permissions = "rwxrwxr--";
        String flexibleCapacity="true";
        int size = 100;
        CreateController.CreateDirReq createDirReq = new CreateController.CreateDirReq(pathName, owner, group, permissions, size);

        //when
        MvcResult mvcResult = mockMvc.perform(
                        post(reqUrl)
                                .param("flexibleCapacity", flexibleCapacity)
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
    @DisplayName("[실패] 디렉토리 추가: 중복X, 가장 윗 계층 X, 부모 존재X, 용량 부족, createMissingParent=true, flexibleCapacity=false")
    @Test
    void create9() throws Exception {
        //given
        String reqUrl = "/directory/create";
        String pathName = "/test/abc/ade";
        String owner = "root";
        String group = "rootGroup";
        String permissions = "rwxrwxr--";
        String createMissingParent = "true";
        String flexibleCapacity="false";
        int size = 100;
        CreateController.CreateDirReq createDirReq = new CreateController.CreateDirReq(pathName, owner, group, permissions, size);

        //when
        MvcResult mvcResult = mockMvc.perform(
                        post(reqUrl)
                                .param("createMissingParent", createMissingParent)
                                .param("flexibleCapacity", flexibleCapacity)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new Gson().toJson(createDirReq)))
                .andDo(print())
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        SuccessResult responseBody = mapper.readValue(mvcResult.getResponse().getContentAsString(), SuccessResult.class);

        //then
        assertThat(responseBody.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    @DisplayName("[성공] 디렉토리 추가: 중복X, 가장 윗 계층 X, 부모 존재X, 용량 부족, 요청 디렉토리 개수 <= 남은 용량, createMissingParent=true, flexibleCapacity=true")
    @Test
    void create10() throws Exception {
        //given
        String reqUrl = "/directory/create";
        String pathName = "/test/abc/ade";
        String owner = "root";
        String group = "rootGroup";
        String permissions = "rwxrwxr--";
        String createMissingParent = "true";
        String flexibleCapacity="true";
        int size = 100;
        CreateController.CreateDirReq createDirReq = new CreateController.CreateDirReq(pathName, owner, group, permissions, size);

        //when
        MvcResult mvcResult = mockMvc.perform(
                        post(reqUrl)
                                .param("createMissingParent", createMissingParent)
                                .param("flexibleCapacity", flexibleCapacity)
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
    @DisplayName("[실패] 디렉토리 추가: 중복X, 가장 윗 계층 X, 부모 존재X, 용량 부족, 요청 디렉토리 개수 > 남은 용량, createMissingParent=true, flexibleCapacity=true")
    @Test
    void create11() throws Exception {
        //given
        String reqUrl = "/directory/create";
        String pathName = "/test/abc/ade/ade/weq/qwe/qwe/qwe/qwe/qwe/asd/w";
        String owner = "root";
        String group = "rootGroup";
        String permissions = "rwxrwxr--";
        String createMissingParent = "true";
        String flexibleCapacity="true";
        int size = 100;
        CreateController.CreateDirReq createDirReq = new CreateController.CreateDirReq(pathName, owner, group, permissions, size);

        //when
        MvcResult mvcResult = mockMvc.perform(
                        post(reqUrl)
                                .param("createMissingParent", createMissingParent)
                                .param("flexibleCapacity", flexibleCapacity)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new Gson().toJson(createDirReq)))
                .andDo(print())
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        SuccessResult responseBody = mapper.readValue(mvcResult.getResponse().getContentAsString(), SuccessResult.class);

        //then
        assertThat(responseBody.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Nested
    class useMockRepository {
        @Autowired
        private MockMvc mockMvc;
        @MockBean
        private DirectoryRepository mockRepository;

        @Value("${config.capacity}")
        private int totalCapacity;

        @DisplayName("[실패] 디렉토리 추가: 용량 없음")
        @Test
        void create12() throws Exception {
            //given
            String reqUrl = "/directory/create";
            String pathName = "/test";
            String owner = "root";
            String group = "rootGroup";
            String permissions = "rwxrwxr--";
            int size = 100;
            CreateController.CreateDirReq createDirReq = new CreateController.CreateDirReq(pathName, owner, group, permissions, size);

            when(mockRepository.getAllUsedCapacity()).thenReturn(totalCapacity);

            //when
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

    }
}