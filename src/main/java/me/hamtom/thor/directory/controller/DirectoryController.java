package me.hamtom.thor.directory.controller;

import me.hamtom.thor.directory.controller.response.Result;
import me.hamtom.thor.directory.controller.response.SuccessResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DirectoryController {
    @PostMapping("/directory/create")
    public ResponseEntity<Result> createDirectory() {

        return ResponseEntity.ok(new SuccessResult(null));
    }
    @PostMapping("/directory/rename")
    public ResponseEntity<Result> renameDirectory() {

        return ResponseEntity.ok(new SuccessResult(null));
    }
    @PostMapping("/directory/move")
    public ResponseEntity<Result> moveDirectory() {

        return ResponseEntity.ok(new SuccessResult(null));
    }
    @GetMapping("/directory/info")
    public ResponseEntity<Result> lookupDirectory() {

        return ResponseEntity.ok(new SuccessResult(null));
    }
    @GetMapping("/directory/sublist")
    public ResponseEntity<Result> getSublistDirectory() {

        return ResponseEntity.ok(new SuccessResult(null));
    }
    @DeleteMapping("/directory/remove")
    public ResponseEntity<Result> deleteDirectory() {

        return ResponseEntity.ok(new SuccessResult(null));
    }


}
