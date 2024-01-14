package me.hamtom.thor.directory.domain.migrate;

import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class MoveController {

    private final MigrateService service;

    @PostMapping("/directory/move")
    public ResponseEntity<Result> moveDirectory(
            @RequestBody @Valid MoveDirReq moveDirReq,
            @RequestParam(required = false) @OptionValid String mergeOnDuplicate
    ) {
        log.info("디렉토리 이동 요청, mergeOnDuplicate: {}, body: {}", mergeOnDuplicate, moveDirReq.toString());

        //option to boolean
        boolean isMergeOnDuplicate = optionToBoolean(mergeOnDuplicate);

        //req to migrateCommand
        String toMigratePath = service.generateToMigratePathForMove(moveDirReq.sourcePath, moveDirReq.targetPath);
        MigrateCommand command = moveDirReq.toMigrateCommand(toMigratePath, isMergeOnDuplicate);

        //move result
        MigrateResult result = service.migrateDirectory(command);
        log.info("디렉토리 이동 응답, body: {}", result.toString());

        return ResponseEntity.ok(new SuccessResult(result));
    }


    @Data
    static class MoveDirReq {
        @PathValid
        private String sourcePath;
        @PathValid
        private String targetPath;

        public MigrateCommand toMigrateCommand(String toMigratePath, boolean isMergeOnDuplicate) {
            return new MigrateCommand(sourcePath, toMigratePath, isMergeOnDuplicate);
        }
    }
}
