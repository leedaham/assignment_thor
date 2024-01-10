package me.hamtom.thor.directory.controller;

import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.hamtom.thor.directory.dto.CreateDirectoryDto;
import me.hamtom.thor.directory.controller.response.Result;
import me.hamtom.thor.directory.controller.response.SuccessResult;
import me.hamtom.thor.directory.dto.CreateDirectoryResultDto;
import me.hamtom.thor.directory.dto.enumerated.OptionValue;
import me.hamtom.thor.directory.service.CreateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static me.hamtom.thor.directory.controller.validate.ValidatorHelper.strToOptionValue;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CreateController {

    private final CreateService createService;


    @PostMapping("/directory/create")
    public ResponseEntity<Result> createDirectory(
            @RequestBody @Valid CreateDirReq createDirReq,
            @RequestParam(required = false) String createMissingParent,
            @RequestParam(required = false) String flexibleCapacity
    ) {
        log.info("생성 요청, createMissingParent: {}, flexibleCapacity: {}, body: {}", createMissingParent, flexibleCapacity, createDirReq.toString());

        //Option parameter 검증
        OptionValue checkedCreateMissingParent = strToOptionValue("createMissingParent", createMissingParent);
        OptionValue checkedFlexibleCapacity = strToOptionValue("flexibleCapacity", flexibleCapacity);
        log.trace("Option parameter 검증 완료");

        //디렉토리 생성 DTO
        CreateDirectoryDto createDirectoryDto = createDirReq.convertToCreateDirectoryDto(checkedCreateMissingParent, checkedFlexibleCapacity);

        //디렉토리 생성 및 결과
        CreateDirectoryResultDto createDirectoryResult = createService.createDirectory(createDirectoryDto);
        log.info("생성 응답, body: {}", createDirectoryResult.toString());

        return ResponseEntity.ok(new SuccessResult(createDirectoryResult));
    }


    //TODO
    //check validation
    @Data
    static class CreateDirReq {
        private String pathName;
        private String owner;
        private String group;
        private String permissions;
        private int size;

        public CreateDirectoryDto convertToCreateDirectoryDto(OptionValue checkedCreateMissingParent, OptionValue flexibleCapacity) {
            return new CreateDirectoryDto(pathName, owner, group, permissions, size, checkedCreateMissingParent, flexibleCapacity);
        }
    }
}
