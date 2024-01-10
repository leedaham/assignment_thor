package me.hamtom.thor.directory.domain.common.response;

import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * 성공 응답 객체
 */
@Data
public class SuccessResult implements Result{
    private HttpStatus httpStatus = HttpStatus.OK;
    private String message = "요청이 성공적으로 처리되었습니다.";
    private Object data;

    public SuccessResult(Object data) {
        this.data = data;
    }
}
