package me.hamtom.thor.directory.domain.rename;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.hamtom.thor.directory.domain.common.response.Result;
import me.hamtom.thor.directory.domain.common.response.SuccessResult;
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
public class RenameController {

    @PostMapping("/directory/rename")
    public ResponseEntity<Result> renameDirectory(
            @RequestBody @Valid RenameDirReq renameDirReq,
            @RequestParam(required = false) String mergeOnDuplicate
    ) {
        //Option parameter 검증
        OptionValue mergeOnDuplicateOptionValue = strToOptionValue("mergeOnDuplicate", mergeOnDuplicate);
        log.trace("Option parameter 검증 완료");

        //디렉토리 rename DTO
        RenameDirectoryDto renameDirectoryDto = renameDirReq.createRenameDirectoryDto(mergeOnDuplicateOptionValue);

        //디렉토리 rename 및 결과

        return ResponseEntity.ok(new SuccessResult(null));
    }

    @Data
    static class RenameDirReq {
        private String oldPathName;
        private String newName;

        public RenameDirectoryDto createRenameDirectoryDto(OptionValue mergeOnDuplicate) {
            return new RenameDirectoryDto(oldPathName, newName, mergeOnDuplicate);
        }
    }

    @Data
    @AllArgsConstructor
    public static class RenameDirectoryDto {
        private String oldPathName;
        private String newName;
        private OptionValue mergeOnDuplicate;
    }
}
