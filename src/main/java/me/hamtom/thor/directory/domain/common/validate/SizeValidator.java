package me.hamtom.thor.directory.domain.common.validate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;

import java.util.regex.Pattern;

public class SizeValidator implements ConstraintValidator<SizeValid, Integer> {

    @Value("${config.request.validator.size.min}")
    private int sizeMinValue;
    @Value("${config.request.validator.size.max}")
    private int sizeMaxValue;
    @Value("${config.request.validator.size.message.min-max}")
    private String minMaxMsg;


    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        // Null 확인, Null 처리는 Controller, Req 객체에서
        if (value == null){
            return true;
        }

        if (value < sizeMinValue || value > sizeMaxValue) {
            minMaxMsg = String.format(minMaxMsg, sizeMinValue, sizeMaxValue);
            customMessageForValidation(context, minMaxMsg);
            return false;
        }

        return true;
    }


    private void customMessageForValidation(ConstraintValidatorContext context, String message) {
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }

}
