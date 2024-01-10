package me.hamtom.thor.directory.domain.getSublist;

import me.hamtom.thor.directory.domain.common.response.Result;
import me.hamtom.thor.directory.domain.common.response.SuccessResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetSublistController {
    @GetMapping("/directory/sublist")
    public ResponseEntity<Result> getSublistDirectory() {

        return ResponseEntity.ok(new SuccessResult(null));
    }
}
