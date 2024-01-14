package me.hamtom.thor.directory.domain.sublist;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.hamtom.thor.directory.domain.common.response.Result;
import me.hamtom.thor.directory.domain.common.response.SuccessResult;
import me.hamtom.thor.directory.domain.common.validate.PathValid;
import me.hamtom.thor.directory.domain.sublist.dto.GetSublistCommand;
import me.hamtom.thor.directory.domain.sublist.dto.GetSublistResult;
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
            @PathVariable(name = "pathName") @PathValid String pathName
    ) {
        log.info("디렉토리 서브 디렉토리 리스트 조회 요청, pathName: {}", pathName);

        //req to getSublistCommand
        GetSublistCommand command = new GetSublistCommand(pathName);

        //디렉토리 get sublist 결과
        GetSublistResult result = getSublistService.getSublist(command);
        log.info("디렉토리 서브 디렉토리 리스트 조회 응답, body: {}", result.toString());

        return ResponseEntity.ok(new SuccessResult(result));
    }
}
