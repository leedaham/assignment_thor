package me.hamtom.thor.directory.domain.move;

import me.hamtom.thor.directory.domain.common.response.Result;
import me.hamtom.thor.directory.domain.common.response.SuccessResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MoveController {

    @PostMapping("/directory/move")
    public ResponseEntity<Result> moveDirectory() {

        return ResponseEntity.ok(new SuccessResult(null));
    }


}
