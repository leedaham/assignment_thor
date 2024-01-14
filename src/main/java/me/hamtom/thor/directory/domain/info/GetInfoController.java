package me.hamtom.thor.directory.domain.info;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.hamtom.thor.directory.domain.common.response.Result;
import me.hamtom.thor.directory.domain.common.response.SuccessResult;
import me.hamtom.thor.directory.domain.common.validate.PathValid;
import me.hamtom.thor.directory.domain.info.dto.GetInfoCommand;
import me.hamtom.thor.directory.domain.info.dto.GetInfoResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GetInfoController {

    private final GetInfoService getInfoService;
    @GetMapping("/directory/info/{pathName}")
    public ResponseEntity<Result> getDirectoryInfo(
            @PathVariable(name = "pathName") @PathValid String pathName
    ) {
        log.info("디렉토리 정보 조회 요청, pathName: {}", pathName);

        //req to getInfoCommand
        GetInfoCommand command = new GetInfoCommand(pathName);

        //get info result
        GetInfoResult result = getInfoService.getDirectoryInfo(command);
        log.info("디렉토리 정보 조회 응답, body: {}", result.toString());

        return ResponseEntity.ok(new SuccessResult(result));
    }
}
