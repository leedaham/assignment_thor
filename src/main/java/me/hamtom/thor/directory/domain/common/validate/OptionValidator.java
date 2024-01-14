package me.hamtom.thor.directory.domain.common.validate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;

import java.util.regex.Pattern;

public class OptionValidator implements ConstraintValidator<OptionValid, String> {

    @Value("${config.request.validator.option-query-string.message}")
    private String optionMsg;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        // Null 확인, Null 처리는 Controller, Req 객체에서
        //true, false 확인.
        if ((value != null) && !value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")) {
            customMessageForValidation(context, optionMsg);
            return false;
        }
        return true;
    }


    private void customMessageForValidation(ConstraintValidatorContext context, String message) {
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
