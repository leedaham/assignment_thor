package me.hamtom.thor.directory.domain.create;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.hamtom.thor.directory.domain.common.validate.*;
import me.hamtom.thor.directory.domain.create.dto.CreateCommand;
import me.hamtom.thor.directory.domain.common.response.Result;
import me.hamtom.thor.directory.domain.common.response.SuccessResult;
import me.hamtom.thor.directory.domain.create.dto.CreateResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static me.hamtom.thor.directory.domain.common.validate.ValidatorHelper.optionToBoolean;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CreateController {

    private final CreateService service;

    @PostMapping("/directory/create")
    public ResponseEntity<Result> createDirectory(
            @RequestBody @Valid CreateDirReq createDirReq,
            @RequestParam(required = false) @OptionValid String createMissingParent,
            @RequestParam(required = false) @OptionValid String flexibleCapacity
    ) {
        log.info("디렉토리 생성 요청, createMissingParent: {}, flexibleCapacity: {}, body: {}", createMissingParent, flexibleCapacity, createDirReq.toString());

        //option to boolean
        boolean isCreateMissingParent = optionToBoolean(createMissingParent);
        boolean isFlexibleCapacity = optionToBoolean(flexibleCapacity);

        //req to CreateCommand
        CreateCommand command = createDirReq.toCreateCommand(isCreateMissingParent, isFlexibleCapacity);

        //디렉토리 create 및 결과
        CreateResult result = service.createDirectory(command);
        log.info("디렉토리 생성 응답, body: {}", result.toString());

        return ResponseEntity.ok(new SuccessResult(result));
    }


    @Data
    @AllArgsConstructor
    static class CreateDirReq {
        @PathValid
        @NotBlank(message = "pathName은 필수 값입니다.")
        private String pathName;
        @OwnerGroupValid
        @NotBlank(message = "owner는 필수 값입니다.")
        private String owner;
        @OwnerGroupValid
        @NotBlank(message = "group은 필수 값입니다.")
        private String group;
        @PermissionsValid
        @NotBlank(message = "permissions는 필수 값입니다.")
        private String permissions;
        @SizeValid
        private int size;

        public CreateCommand toCreateCommand(boolean isCreateMissingParent, boolean isFlexibleCapacity) {
            return new CreateCommand(pathName, owner, group, permissions, size, isCreateMissingParent, isFlexibleCapacity);
        }
    }
}
