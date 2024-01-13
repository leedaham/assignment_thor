package me.hamtom.thor.directory.domain.getSublist;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.hamtom.thor.directory.domain.common.response.Result;
import me.hamtom.thor.directory.domain.common.response.SuccessResult;
import me.hamtom.thor.directory.domain.getSublist.dto.GetSublistCommand;
import me.hamtom.thor.directory.domain.getSublist.dto.GetSublistResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GetSublistController {

    private final GetSublistService getSublistService;

    @GetMapping("/directory/sublist/{pathName}")
    public ResponseEntity<Result> getSublistDirectory(
            @PathVariable(name = "pathName") String pathName
    ) {
        //디렉토리 get sublist DTO
        GetSublistCommand command = new GetSublistCommand(pathName);

        //디렉토리 get sublist 결과
        GetSublistResult result = getSublistService.getSublist(command);

        return ResponseEntity.ok(new SuccessResult(result));
    }
}
