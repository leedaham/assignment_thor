package me.hamtom.thor.directory.domain.create;

import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.hamtom.thor.directory.domain.create.dto.CreateDirectoryCommand;
import me.hamtom.thor.directory.domain.common.response.Result;
import me.hamtom.thor.directory.domain.common.response.SuccessResult;
import me.hamtom.thor.directory.domain.create.dto.CreateDirectoryResult;
import me.hamtom.thor.directory.domain.common.enumerated.OptionValue;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static me.hamtom.thor.directory.domain.common.validate.ValidatorHelper.strToOptionValue;

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
        OptionValue createMissingParentOptionValue = strToOptionValue("createMissingParent", createMissingParent);
        OptionValue flexibleCapacityOptionValue = strToOptionValue("flexibleCapacity", flexibleCapacity);
        log.trace("Option parameter 검증 완료");

        //디렉토리 create command
        CreateDirectoryCommand command = createDirReq.toCommand(createMissingParentOptionValue, flexibleCapacityOptionValue);

        //디렉토리 create 및 결과
        CreateDirectoryResult result = createService.createDirectory(command);
        log.info("생성 응답, body: {}", result.toString());

        return ResponseEntity.ok(new SuccessResult(result));
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

        public CreateDirectoryCommand toCommand(OptionValue checkedCreateMissingParent, OptionValue flexibleCapacity) {
            return new CreateDirectoryCommand(pathName, owner, group, permissions, size, checkedCreateMissingParent, flexibleCapacity);
        }
    }
}
