package me.hamtom.thor.directory.domain.migrate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import me.hamtom.thor.directory.domain.common.entity.Directory;
import me.hamtom.thor.directory.domain.common.repository.DirectoryRepository;
import me.hamtom.thor.directory.domain.common.response.SuccessResult;
import me.hamtom.thor.directory.domain.create.CreateService;
import me.hamtom.thor.directory.domain.create.dto.CreateCommand;
import me.hamtom.thor.directory.domain.migrate.dto.MigrateResult;
import me.hamtom.thor.directory.domain.migrate.enumerated.MergeStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MoveControllerTest {
    @PersistenceContext
    EntityManager em;
    @Inject
    private MockMvc mockMvc;

    @Autowired
    private CreateService createService;
    @Autowired
    private MigrateService migrateService;

    @Autowired
    private DirectoryRepository directoryRepository;

    private final String owner = "root";
    private final String group = "rootGroup";
    private final String permissions = "rwxrwxrwx";
    private final int size = 100;

    @DisplayName("[실패] 디렉토리 이동: 존재하지 않는 디렉토리 아래로 이동")
    @Test
    void move1() throws Exception {
        //given
        String reqUrl = "/directory/move";
        String sourcePath = "/abc/old";
        String targetPath = "/def/new";
        MoveController.MoveDirReq moveDirReq = new MoveController.MoveDirReq(sourcePath, targetPath);
        String mergeOnDuplicate = "false";


        //when
        createService.createDirectory(new CreateCommand(sourcePath, owner, group, permissions, size, true, true));
        MvcResult mvcResult = mockMvc.perform(
                        post(reqUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new Gson().toJson(moveDirReq)))
                .andDo(print())
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        SuccessResult responseBody = mapper.readValue(mvcResult.getResponse().getContentAsString(), SuccessResult.class);

        //then
        assertThat(responseBody.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    @DisplayName("[성공] 디렉토리 이동: 중복X, 자식 존재X")
    @Test
    void move2() throws Exception {
        //given
        String reqUrl = "/directory/move";
        String sourcePath = "/abc/old";
        String targetPath = "/def/new";
        MoveController.MoveDirReq moveDirReq = new MoveController.MoveDirReq(sourcePath, targetPath);
        String mergeOnDuplicate = "false";


        //when
        createService.createDirectory(new CreateCommand(sourcePath, owner, group, permissions, size, true, true));
        createService.createDirectory(new CreateCommand(targetPath, owner, group, permissions, size, true, true));
        MvcResult mvcResult = mockMvc.perform(
                        post(reqUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new Gson().toJson(moveDirReq)))
                .andDo(print())
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        SuccessResult responseBody = mapper.readValue(mvcResult.getResponse().getContentAsString(), SuccessResult.class);
        MigrateResult result = mapper.convertValue(responseBody.getData(), MigrateResult.class);

        String toMigratePath = migrateService.generateToMigratePathForMove(sourcePath, targetPath);
        Directory directory = directoryRepository.findByPathName(toMigratePath).get();

        //then
        assertThat(responseBody.getHttpStatus()).isEqualTo(HttpStatus.OK);
        assertThat(directory.getPathName()).isEqualTo(result.getToMigratePath());
        assertThat(result.getMergeStatus()).isEqualTo(MergeStatus.NOT_MERGE);
    }
    @DisplayName("[성공] 디렉토리 이동: 중복, 자식 존재X - 병합, mergeOnDuplicate=true")
    @Test
    void move3() throws Exception {
        //given
        String reqUrl = "/directory/move";
        String sourcePath = "/abc/old";
        String targetPath = "/def/new";
        MoveController.MoveDirReq moveDirReq = new MoveController.MoveDirReq(sourcePath, targetPath);
        String mergeOnDuplicate = "true";


        //when
        createService.createDirectory(new CreateCommand(sourcePath, owner, group, permissions, size, true, true));
        createService.createDirectory(new CreateCommand(targetPath+"/old", owner, group, permissions, size, true, true));
        MvcResult mvcResult = mockMvc.perform(
                        post(reqUrl)
                                .param("mergeOnDuplicate", mergeOnDuplicate)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new Gson().toJson(moveDirReq)))
                .andDo(print())
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        SuccessResult responseBody = mapper.readValue(mvcResult.getResponse().getContentAsString(), SuccessResult.class);
        MigrateResult result = mapper.convertValue(responseBody.getData(), MigrateResult.class);

        String toMigratePath = migrateService.generateToMigratePathForMove(sourcePath, targetPath);
        Directory directory = directoryRepository.findByPathName(toMigratePath).get();

        //then
        assertThat(responseBody.getHttpStatus()).isEqualTo(HttpStatus.OK);
        assertThat(directory.getPathName()).isEqualTo(result.getToMigratePath());
        assertThat(result.getMergeStatus()).isEqualTo(MergeStatus.MERGE);
    }
    @DisplayName("[실패] 디렉토리 이동: 중복, 자식 존재X - 병합X, mergeOnDuplicate=false")
    @Test
    void move4() throws Exception {
        //given
        String reqUrl = "/directory/move";
        String sourcePath = "/abc/old";
        String targetPath = "/def/new";
        MoveController.MoveDirReq moveDirReq = new MoveController.MoveDirReq(sourcePath, targetPath);
        String mergeOnDuplicate = "false";


        //when
        createService.createDirectory(new CreateCommand(sourcePath, owner, group, permissions, size, true, true));
        createService.createDirectory(new CreateCommand(targetPath+"/old", owner, group, permissions, size, true, true));
        MvcResult mvcResult = mockMvc.perform(
                        post(reqUrl)
                                .param("mergeOnDuplicate", mergeOnDuplicate)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new Gson().toJson(moveDirReq)))
                .andDo(print())
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        SuccessResult responseBody = mapper.readValue(mvcResult.getResponse().getContentAsString(), SuccessResult.class);

        //then
        assertThat(responseBody.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    @DisplayName("[실패] 디렉토리 이동: 기존, 변경 경로 똑같음")
    @Test
    void move5() throws Exception {
        //given
        String reqUrl = "/directory/move";
        String sourcePath = "/abc/old";
        String targetPath = "/abc";
        MoveController.MoveDirReq moveDirReq = new MoveController.MoveDirReq(sourcePath, targetPath);
        String mergeOnDuplicate = "false";


        //when
        createService.createDirectory(new CreateCommand(sourcePath, owner, group, permissions, size, true, true));
        MvcResult mvcResult = mockMvc.perform(
                        post(reqUrl)
                                .param("mergeOnDuplicate", mergeOnDuplicate)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new Gson().toJson(moveDirReq)))
                .andDo(print())
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        SuccessResult responseBody = mapper.readValue(mvcResult.getResponse().getContentAsString(), SuccessResult.class);

        //then
        assertThat(responseBody.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    @DisplayName("[성공] 디렉토리 이동: 복수, 중복 - 병합, mergeOnDuplicate=true")
    @Test
    void move6() throws Exception {
        //given
        String reqUrl = "/directory/rename";
        String oldPathName = "/abc";
        String newName = "new";
        RenameController.RenameDirReq renameDirReq = new RenameController.RenameDirReq(oldPathName, newName);
        String mergeOnDuplicate = "true";


        //when
        createService.createDirectory(new CreateCommand("/abc/qwe/qwe", owner, group, permissions, size, true, true));
        createService.createDirectory(new CreateCommand("/new/qwe", owner, group, permissions, size, true, true));
        MvcResult mvcResult = mockMvc.perform(
                        post(reqUrl)
                                .param("mergeOnDuplicate", mergeOnDuplicate)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new Gson().toJson(renameDirReq)))
                .andDo(print())
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        SuccessResult responseBody = mapper.readValue(mvcResult.getResponse().getContentAsString(), SuccessResult.class);
        MigrateResult result = mapper.convertValue(responseBody.getData(), MigrateResult.class);

        em.clear();
        String toMigratePath = migrateService.generateToMigratePathForRename(oldPathName, newName);
        Directory directory = directoryRepository.findByPathName(toMigratePath).get();

        //then
        assertThat(responseBody.getHttpStatus()).isEqualTo(HttpStatus.OK);
        assertThat(directory.getPathName()).isEqualTo(result.getToMigratePath());
        assertThat(result.getMigratedChildDirectories()).isEqualTo(List.of("/new/qwe/qwe"));
        assertThat(result.getMergedChildDirectories()).isEqualTo(List.of("/new/qwe"));
        assertThat(result.getMergeStatus()).isEqualTo(MergeStatus.MERGE);
    }
    @DisplayName("[성공] 디렉토리 이동: 중복X, 자식 존재")
    @Test
    void move7() throws Exception {
        //given
        String reqUrl = "/directory/move";
        String sourcePath = "/abc/old";
        String targetPath = "/def/new";
        MoveController.MoveDirReq moveDirReq = new MoveController.MoveDirReq(sourcePath, targetPath);
        String mergeOnDuplicate = "false";


        //when
        createService.createDirectory(new CreateCommand(sourcePath+"/son", owner, group, permissions, size, true, true));
        createService.createDirectory(new CreateCommand(targetPath, owner, group, permissions, size, true, true));
        MvcResult mvcResult = mockMvc.perform(
                        post(reqUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new Gson().toJson(moveDirReq)))
                .andDo(print())
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        SuccessResult responseBody = mapper.readValue(mvcResult.getResponse().getContentAsString(), SuccessResult.class);
        MigrateResult result = mapper.convertValue(responseBody.getData(), MigrateResult.class);

        em.clear();
        String toMigratePath = migrateService.generateToMigratePathForMove(sourcePath, targetPath);
        Directory directory = directoryRepository.findByPathName(toMigratePath).get();

        //then
        assertThat(responseBody.getHttpStatus()).isEqualTo(HttpStatus.OK);
        assertThat(directory.getPathName()).isEqualTo(result.getToMigratePath());
        assertThat(List.of("/def/new/old/son")).isEqualTo(result.getMigratedChildDirectories());
        assertThat(result.getMergeStatus()).isEqualTo(MergeStatus.NOT_MERGE);
    }
    @DisplayName("[성공] 디렉토리 이동: 중복, 자식 존재 - 병합, mergeOnDuplicate=true")
    @Test
    void move8() throws Exception {
        String reqUrl = "/directory/move";
        String sourcePath = "/abc/old";
        String targetPath = "/def/new";
        MoveController.MoveDirReq moveDirReq = new MoveController.MoveDirReq(sourcePath, targetPath);
        String mergeOnDuplicate = "true";


        //when
        createService.createDirectory(new CreateCommand(sourcePath+"/son/kim", owner, group, permissions, size, true, true));
        createService.createDirectory(new CreateCommand(targetPath+"/old/son", owner, group, permissions, size, true, true));
        MvcResult mvcResult = mockMvc.perform(
                        post(reqUrl)
                                .param("mergeOnDuplicate", mergeOnDuplicate)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new Gson().toJson(moveDirReq)))
                .andDo(print())
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        SuccessResult responseBody = mapper.readValue(mvcResult.getResponse().getContentAsString(), SuccessResult.class);
        MigrateResult result = mapper.convertValue(responseBody.getData(), MigrateResult.class);

        em.clear();
        String toMigratePath = migrateService.generateToMigratePathForMove(sourcePath, targetPath);
        Directory directory = directoryRepository.findByPathName(toMigratePath).get();

        //then
        assertThat(responseBody.getHttpStatus()).isEqualTo(HttpStatus.OK);
        assertThat(directory.getPathName()).isEqualTo(result.getToMigratePath());
        assertThat(List.of("/def/new/old/son/kim")).isEqualTo(result.getMigratedChildDirectories());
        assertThat(List.of("/def/new/old/son")).isEqualTo(result.getMergedChildDirectories());
        assertThat(result.getMergeStatus()).isEqualTo(MergeStatus.MERGE);
    }
    @DisplayName("[실패] 디렉토리 이동: 중복, 자식 존재 - 병합X, mergeOnDuplicate=false")
    @Test
    void move9() throws Exception {
        String reqUrl = "/directory/move";
        String sourcePath = "/abc/old";
        String targetPath = "/def/new";
        MoveController.MoveDirReq moveDirReq = new MoveController.MoveDirReq(sourcePath, targetPath);
        String mergeOnDuplicate = "false";


        //when
        createService.createDirectory(new CreateCommand(sourcePath+"/son/kim", owner, group, permissions, size, true, true));
        createService.createDirectory(new CreateCommand(targetPath+"/old/son", owner, group, permissions, size, true, true));
        MvcResult mvcResult = mockMvc.perform(
                        post(reqUrl)
                                .param("mergeOnDuplicate", mergeOnDuplicate)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new Gson().toJson(moveDirReq)))
                .andDo(print())
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        SuccessResult responseBody = mapper.readValue(mvcResult.getResponse().getContentAsString(), SuccessResult.class);

        //then
        assertThat(responseBody.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    @DisplayName("[실패] 디렉토리 이동: / 디렉토리 이동")
    @Test
    void move10() throws Exception {
        //given
        String reqUrl = "/directory/move";
        String sourcePath = "/";
        String targetPath = "/def/new";
        MoveController.MoveDirReq moveDirReq = new MoveController.MoveDirReq(sourcePath, targetPath);
        String mergeOnDuplicate = "false";

        //when
        MvcResult mvcResult = mockMvc.perform(
                        post(reqUrl)
                                .param("mergeOnDuplicate", mergeOnDuplicate)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new Gson().toJson(moveDirReq)))
                .andDo(print())
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        SuccessResult responseBody = mapper.readValue(mvcResult.getResponse().getContentAsString(), SuccessResult.class);

        //then
        assertThat(responseBody.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}