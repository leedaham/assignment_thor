package me.hamtom.thor.directory.controller;

import me.hamtom.thor.directory.controller.response.Result;
import me.hamtom.thor.directory.controller.response.SuccessResult;
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
