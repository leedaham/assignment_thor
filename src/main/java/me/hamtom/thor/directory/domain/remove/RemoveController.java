package me.hamtom.thor.directory.domain.remove;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.hamtom.thor.directory.domain.common.response.Result;
import me.hamtom.thor.directory.domain.common.response.SuccessResult;
import me.hamtom.thor.directory.domain.common.validate.OptionValid;
import me.hamtom.thor.directory.domain.common.validate.PathValid;
import me.hamtom.thor.directory.domain.remove.dto.RemoveCommand;
import me.hamtom.thor.directory.domain.remove.dto.RemoveDirectoryResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static me.hamtom.thor.directory.domain.common.validate.ValidatorHelper.optionToBoolean;


@Slf4j
@RestController
@RequiredArgsConstructor
public class RemoveController {

    private final RemoveService removeService;

    @DeleteMapping("/directory/remove/{pathName}")
    public ResponseEntity<Result> deleteDirectory(
            @PathVariable(name = "pathName") @PathValid String pathName,
            @RequestParam(required = false) @OptionValid String removeWithChild
    ) {
        log.info("디렉토리 제거 요청, pathName: {}, removeWithChild: {}", pathName, removeWithChild);

        //option to boolean
        boolean isRemoveWithChild = optionToBoolean(removeWithChild);

        //req to removeCommand
        RemoveCommand command = toRemoveCommand(pathName, isRemoveWithChild);

        //remove result
        RemoveDirectoryResult result = removeService.removeDirectory(command);
        log.info("디렉토리 제거 응답, body: {}", result.toString());

        return ResponseEntity.ok(new SuccessResult(result));
    }

    private RemoveCommand toRemoveCommand(String pathName, boolean isRemoveWithChild) {
        return new RemoveCommand(pathName, isRemoveWithChild);
    }

}
