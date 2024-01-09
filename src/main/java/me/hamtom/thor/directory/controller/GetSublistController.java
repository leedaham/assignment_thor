package me.hamtom.thor.directory.controller;

import me.hamtom.thor.directory.controller.response.Result;
import me.hamtom.thor.directory.controller.response.SuccessResult;
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
