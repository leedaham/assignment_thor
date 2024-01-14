package me.hamtom.thor.directory.domain.migrate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.hamtom.thor.directory.domain.common.validate.DirNameValid;
import me.hamtom.thor.directory.domain.common.validate.OptionValid;
import me.hamtom.thor.directory.domain.common.validate.PathValid;
import me.hamtom.thor.directory.domain.migrate.dto.MigrateCommand;
import me.hamtom.thor.directory.domain.migrate.dto.MigrateResult;
import me.hamtom.thor.directory.domain.common.response.Result;
import me.hamtom.thor.directory.domain.common.response.SuccessResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static me.hamtom.thor.directory.domain.common.validate.ValidatorHelper.optionToBoolean;


@Slf4j
@RestController
@RequiredArgsConstructor
public class RenameController {

    private final MigrateService service;

    @PostMapping("/directory/rename")
    public ResponseEntity<Result> renameDirectory(
            @RequestBody @Valid RenameDirReq renameDirReq,
            @RequestParam(required = false) @OptionValid String mergeOnDuplicate
    ) {
        log.info("디렉토리 이름 변경 요청, mergeOnDuplicate: {}, body: {}", mergeOnDuplicate, renameDirReq.toString());

        //option to boolean
        boolean isMergeOnDuplicate = optionToBoolean(mergeOnDuplicate);

        //req to migrateCommand
        String toMigratePath = service.generateToMigratePathForRename(renameDirReq.oldPathName, renameDirReq.newName);
        MigrateCommand command = renameDirReq.toMigrateCommand(toMigratePath, isMergeOnDuplicate);

        //rename result
        MigrateResult result = service.migrateDirectory(command);
        log.info("디렉토리 이름 변경 응답, body: {}", result.toString());

        return ResponseEntity.ok(new SuccessResult(result));
    }


    @Data
    @AllArgsConstructor
    static class RenameDirReq {
        @PathValid
        @NotBlank(message = "oldPathName은 필수 값입니다.")
        private String oldPathName;
        @DirNameValid
        @NotBlank(message = "newName은 필수 값입니다.")
        private String newName;

        public MigrateCommand toMigrateCommand(String toMigratePath, boolean isMergeOnDuplicate) {
            return new MigrateCommand(oldPathName, toMigratePath, isMergeOnDuplicate);
        }
    }


}
