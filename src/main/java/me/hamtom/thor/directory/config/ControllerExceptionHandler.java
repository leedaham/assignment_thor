package me.hamtom.thor.directory.config;

import lombok.extern.slf4j.Slf4j;
import me.hamtom.thor.directory.domain.common.response.ErrorResult;
import me.hamtom.thor.directory.domain.common.response.FailResult;
import me.hamtom.thor.directory.domain.common.response.Result;
import me.hamtom.thor.directory.domain.common.exception.PredictableRuntimeException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

/**
 * 예외 발생 처리
 */
@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {

    /**
     * 직접 정의한 Exception, 예상 가능한 예외 처리에 사용
     * @param ex 직접 정의한 Exception
     * @return 실패 응답 (HttpStatus.BAD_REQUEST)
     */
    @ExceptionHandler(PredictableRuntimeException.class)
    public ResponseEntity<Result> handlePredictableRuntimeException(PredictableRuntimeException ex) {
        String message = ex.getMessage();
        log.warn(message);
        return ResponseEntity.badRequest().body(new FailResult(message));
    }

    /**
     * HTTP Method 잘못된 경우 발생하는 Exception
     * @param ex HttpRequestMethodNotSupportedException
     * @return 실패 응답 (HttpStatus.BAD_REQUEST)
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Result> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        String message = ex.getMessage();
        log.warn(message, ex);
        return ResponseEntity.badRequest().body(new FailResult(message));
    }


    /**
     * Valid 어노테이션에서 검증에 실패할 경우 발생하는 Exception
     * @param ex MethodArgumentNotValidException
     * @return 실패 응답 (HttpStatus.BAD_REQUEST)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "검증 실패";
        log.warn(message, ex);
        return ResponseEntity.badRequest().body(new FailResult(message));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Result> handleHandlerMethodValidationException(HandlerMethodValidationException ex) {
//        String message = ex.getMessage();
        List<ParameterValidationResult> allValidationResults = ex.getAllValidationResults();
        ParameterValidationResult parameterValidationResult = allValidationResults.get(0);
        String message = parameterValidationResult.getResolvableErrors().get(0).getDefaultMessage();
        log.warn(message, ex);
        return ResponseEntity.badRequest().body(new FailResult(message));
    }

    /**
     * RequestHeader 어노테이션에서 발생하는 Exception
     * @param ex MissingRequestHeaderException
     * @return 실패 응답 (HttpStatus.BAD_REQUEST)
     */
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<Result> handleMissingRequestHeaderException(MissingRequestHeaderException ex) {
        String message = ex.getMessage();
        log.warn(message, ex);
        return ResponseEntity.badRequest().body(new FailResult(message));
    }

    /**
     * 잘못된 body, parameter 요청시 발생하는 Exception
     * @param ex HttpMessageNotReadableException
     * @return 실패 응답 (HttpStatus.BAD_REQUEST)
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Result> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String message = ex.getMessage();
        log.warn(message, ex);
        return ResponseEntity.badRequest().body(new FailResult(message));
    }
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Result> handleNoResourceFoundException(NoResourceFoundException ex) {
        String message = ex.getMessage();
        log.warn(message, ex);
        return ResponseEntity.badRequest().body(new FailResult("존재하지 않는 경로입니다. (경로 변수 사용시 '/' -> '%2F')"));
    }

        /**
     * 앞에서 걸려진 예외 외의 예외로 예상하지 못한 Exception
     * @param ex Exception
     * @return 오류 응답 (HttpStatus.INTERNAL_SERVER_ERROR)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result> handleException(Exception ex) {
        String message = ex.getMessage();
        log.error(message, ex);
        return ResponseEntity.internalServerError().body(new ErrorResult("요청이 실패했습니다. 다시 시도 해주세요. 계속해서 문제가 발생한다면 관리자에게 문의해주십시오."));
    }

}
