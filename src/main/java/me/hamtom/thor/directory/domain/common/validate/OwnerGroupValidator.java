package me.hamtom.thor.directory.domain.common.validate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;

import java.util.regex.Pattern;

public class OwnerGroupValidator implements ConstraintValidator<OwnerGroupValid, String> {

    @Value("${config.request.validator.owner-group-name.min}")
    private int ownerGroupNameMinSize;
    @Value("${config.request.validator.owner-group-name.max}")
    private int ownerGroupNameMaxSize;
    @Value("${config.request.validator.owner-group-name.message.no-blank}")
    private String noBlankMsg;
    @Value("${config.request.validator.owner-group-name.message.min-max}")
    private String minMaxMsg;
    @Value("${config.request.validator.owner-group-name.message.pattern-reg}")
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
        if (length < ownerGroupNameMinSize || length > ownerGroupNameMaxSize) {
            String nameMinMaxMsg = String.format(minMaxMsg, ownerGroupNameMinSize, ownerGroupNameMaxSize);
            customMessageForValidation(context, nameMinMaxMsg);
            return false;
        }

        // 숫자,알파벳 허용
        if (!Pattern.matches(ValidatorHelper.OWNER_GROUP_NAME_REG, value)) {
            customMessageForValidation(context, regMsg);
            return false;
        }

        return true;
    }


    private void customMessageForValidation(ConstraintValidatorContext context, String message) {
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
