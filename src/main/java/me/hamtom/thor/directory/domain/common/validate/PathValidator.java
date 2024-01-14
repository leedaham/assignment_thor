package me.hamtom.thor.directory.domain.common.validate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;

import java.util.regex.Pattern;

public class PathValidator implements ConstraintValidator<PathValid, String> {
    @Value("${config.request.validator.path-name.min}")
    private int pathNameMinSize;
    @Value("${config.request.validator.path-name.max}")
    private int pathNameMaxSize;

    @Value("${config.request.validator.path-name.message.no-blank}")
    private String noBlankMsg;
    @Value("${config.request.validator.path-name.message.min-max}")
    private String minMaxMsg;
    @Value("${config.request.validator.path-name.message.pattern-start-with}")
    private String startWithMsg;
    @Value("${config.request.validator.path-name.message.pattern-not-end-with}")
    private String notEndWithMsg;
    @Value("${config.request.validator.path-name.message.pattern-reg}")
    private String regMsg;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        // Null 확인, Null 처리는 Controller, Req 객체에서
        if (value == null || value.isBlank()){
            return true;
        }

        // 빈 칸 포함 확인
        if (value.contains(" ") ){
            customMessageForValidation(context, noBlankMsg);
            return false;
        }

        // 최소, 최대 길이 확인 (root 제외, '/')
        int length = value.length();
        if ((length < pathNameMinSize || length > pathNameMaxSize) && !value.equals("/")) {
            minMaxMsg = String.format(minMaxMsg, pathNameMinSize, pathNameMaxSize);
            customMessageForValidation(context, minMaxMsg);
            return false;
        }

        // '/'로 시작 확인
        if (!value.startsWith("/")) {
            customMessageForValidation(context, startWithMsg);
            return false;
        }
        // 마지막 '/' 허용 안함 (root 제외, '/')
        if (!value.equals("/") && value.endsWith("/")) {
            customMessageForValidation(context, notEndWithMsg);
            return false;
        }
        // 숫자,알파벳,'_','-', '/' 허용, ('/' 연속 사용 제한)
        if (!Pattern.matches(ValidatorHelper.PATH_NAME_REG, value)) {
            customMessageForValidation(context, regMsg);
            return false;
        }

        return true;
    }

    private void customMessageForValidation(ConstraintValidatorContext context, String message) {
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
