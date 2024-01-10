package me.hamtom.thor.directory.domain.remove;

import me.hamtom.thor.directory.domain.common.response.Result;
import me.hamtom.thor.directory.domain.common.response.SuccessResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RemoveController {
    @DeleteMapping("/directory/remove")
    public ResponseEntity<Result> deleteDirectory() {

        return ResponseEntity.ok(new SuccessResult(null));
    }


}
