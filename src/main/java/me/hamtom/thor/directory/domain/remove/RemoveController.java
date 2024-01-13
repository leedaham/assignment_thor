package me.hamtom.thor.directory.domain.remove;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.hamtom.thor.directory.domain.common.enumerated.OptionValue;
import me.hamtom.thor.directory.domain.common.response.Result;
import me.hamtom.thor.directory.domain.common.response.SuccessResult;
import me.hamtom.thor.directory.domain.remove.dto.RemoveDirectoryCommand;
import me.hamtom.thor.directory.domain.remove.dto.RemoveDirectoryResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static me.hamtom.thor.directory.domain.common.validate.ValidatorHelper.strToOptionValue;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RemoveController {

    private final RemoveService removeService;

    @DeleteMapping("/directory/remove/{pathName}")
    public ResponseEntity<Result> deleteDirectory(
            @PathVariable(name = "pathName") String pathName,
            @RequestParam(required = false) String removeWithChild
    ) {
        OptionValue removeWithChildOptionValue = strToOptionValue("removeWithChild", removeWithChild);
        RemoveDirectoryCommand command = new RemoveDirectoryCommand(pathName, removeWithChildOptionValue);
        RemoveDirectoryResult result = removeService.removeDirectory(command);
        return ResponseEntity.ok(new SuccessResult(result));
    }


}
