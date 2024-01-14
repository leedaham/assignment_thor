package me.hamtom.thor.directory.domain.common.validate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;

import java.util.regex.Pattern;

public class PermissionsValidator implements ConstraintValidator<PermissionsValid, String> {

    @Value("${config.request.validator.permissions.length}")
    private int permissionsLength;
    @Value("${config.request.validator.permissions.message.no-blank}")
    private String noBlankMsg;
    @Value("${config.request.validator.permissions.message.length}")
    private String lengthMsg;
    @Value("${config.request.validator.permissions.message.pattern-reg}")
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
        if (length != permissionsLength) {
            lengthMsg = String.format(lengthMsg, permissionsLength);
            customMessageForValidation(context, lengthMsg);
            return false;
        }

        // 권한 형태만 허용 (rwxrwxrwx)
        if (!Pattern.matches(ValidatorHelper.PERMISSIONS_REG, value)) {
            customMessageForValidation(context, regMsg);
            return false;
        }

        return true;
    }


    private void customMessageForValidation(ConstraintValidatorContext context, String message) {
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
