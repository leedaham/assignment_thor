package me.hamtom.thor.directory.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import me.hamtom.thor.directory.controller.helper.PathValid;
import me.hamtom.thor.directory.controller.response.Result;
import me.hamtom.thor.directory.controller.response.SuccessResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CreateController {
    @Value("${config.request.option-default-value.create.create-missing-parent}")
    private String defaultCreateMissingParent;


    @PostMapping("/directory/create")
    public ResponseEntity<Result> createDirectory(
            @RequestBody @Valid CreateDirReq createDirReq,
            @RequestParam String createMissingParent
    ) {
//        checkCreateMissingParent(createMissingParent);

        return ResponseEntity.ok(new SuccessResult(null));
    }


    @Data
    static class CreateDirReq {


//        @NotBlank(message = "pathName 값은 필수 값입니다.")
//        @Size(min = 2, max = 100, message = "pathName 값은 최소 2자리, 최대 100자리의 문자열입니다.")
        @PathValid(message ="custom.")
        private String pathName;
        private String owner;
        private String group;
        private String permissions;
        private int size;
    }
}
