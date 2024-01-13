package me.hamtom.thor.directory.domain.move;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.hamtom.thor.directory.domain.common.enumerated.OptionValue;
import me.hamtom.thor.directory.domain.common.response.Result;
import me.hamtom.thor.directory.domain.common.response.SuccessResult;
import me.hamtom.thor.directory.domain.move.dto.MoveDirectoryCommand;
import me.hamtom.thor.directory.domain.move.dto.MoveDirectoryResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static me.hamtom.thor.directory.domain.common.validate.ValidatorHelper.strToOptionValue;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MoveController {

    private final MoveDirectoryService moveDirectoryService;

    @PostMapping("/directory/move")
    public ResponseEntity<Result> moveDirectory(
            @RequestBody MoveDirReq moveDirReq,
            @RequestParam(required = false) String moveWithChild,
            @RequestParam(required = false) String mergeOnDuplicate
    ) {
        //Option parameter 검증
        OptionValue moveWithChildOptionValue = strToOptionValue("moveWithChild", moveWithChild);
        OptionValue mergeOnDuplicateOptionValue = strToOptionValue("mergeOnDuplicate", mergeOnDuplicate);
        log.trace("Option parameter 검증 완료");

        //디렉토리 move DTO
        MoveDirectoryCommand command = moveDirReq.toCommand(moveWithChildOptionValue, mergeOnDuplicateOptionValue);

        //디렉토리 move 및 결과
        MoveDirectoryResult result = moveDirectoryService.moveDirectory(command);

        return ResponseEntity.ok(new SuccessResult(result));
    }


    @Data
    static class MoveDirReq {
        private String sourcePath;
        private String targetPath;

        public MoveDirectoryCommand toCommand(OptionValue moveWithChild, OptionValue mergeOnDuplicate) {
            return new MoveDirectoryCommand(sourcePath, targetPath, moveWithChild, mergeOnDuplicate);
        }
    }
}
