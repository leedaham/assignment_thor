package me.hamtom.thor.directory.domain.getInfo;

import me.hamtom.thor.directory.domain.common.response.Result;
import me.hamtom.thor.directory.domain.common.response.SuccessResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetInfoController {

    @GetMapping("/directory/info")
    public ResponseEntity<Result> getDirectoryInfo() {

        return ResponseEntity.ok(new SuccessResult(null));
    }

}
