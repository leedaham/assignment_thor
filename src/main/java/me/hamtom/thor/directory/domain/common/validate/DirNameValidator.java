package me.hamtom.thor.directory.domain.common.validate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;

import java.util.regex.Pattern;

public class DirNameValidator implements ConstraintValidator<DirNameValid, String> {

    @Value("${config.request.validator.dir-name.min}")
    private int dirNameMinSize;
    @Value("${config.request.validator.dir-name.max}")
    private int dirNameMaxSize;
    @Value("${config.request.validator.dir-name.message.no-blank}")
    private String noBlankMsg;
    @Value("${config.request.validator.dir-name.message.min-max}")
    private String minMaxMsg;
    @Value("${config.request.validator.dir-name.message.pattern-reg}")
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

        // 최소, 최대 길이 확인
        int length = value.length();
        if (length < dirNameMinSize || length > dirNameMaxSize) {
            minMaxMsg = String.format(minMaxMsg, dirNameMinSize, dirNameMaxSize);
            customMessageForValidation(context, minMaxMsg);
            return false;
        }
        // 숫자,알파벳,'_','-' 허용
        if (!Pattern.matches(ValidatorHelper.DIR_NAME_REG, value)) {
            customMessageForValidation(context, regMsg);
            return false;
        }

        return true;
    }


    private void customMessageForValidation(ConstraintValidatorContext context, String message) {
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
