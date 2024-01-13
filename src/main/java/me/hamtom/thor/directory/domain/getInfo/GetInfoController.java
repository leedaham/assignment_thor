package me.hamtom.thor.directory.domain.getInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.hamtom.thor.directory.domain.common.response.Result;
import me.hamtom.thor.directory.domain.common.response.SuccessResult;
import me.hamtom.thor.directory.domain.getInfo.dto.GetDirectoryInfoResultDto;
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
            @PathVariable(name = "pathName") String pathName
    ) {
        GetDirectoryInfoResultDto directoryInfo = getInfoService.getDirectoryInfo(pathName);
        return ResponseEntity.ok(new SuccessResult(directoryInfo));
    }
}
