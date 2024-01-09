package me.hamtom.thor.directory.controller.helper;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;

import java.util.regex.Pattern;

public class PathValidator implements ConstraintValidator<PathValid, String> {
    @Value("${config.request.validator.path-name.min}")
    private int pathNameMinSize;
    @Value("${config.request.validator.path-name.max}")
    private int pathNameMaxSize;
    @Value("${config.request.validator.dir-name.min}")
    private int dirNameMinSize;
    @Value("${config.request.validator.dir-name.max}")
    private int dirNameMaxSize;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
//        // Null 확인
//        if (value == null){
//            System.out.println("1");
//            return false;
//        }

        // 빈 칸 포함 확인
        if (value.contains(" ")){
            System.out.println("2");
            return false;
        }

//        // 최소 길이 확인
//        if (value.length() < pathNameMinSize) {
//            System.out.println("3");
//            return false;
//        }
//
//        // 최대 길이 확인
//        if (value.length() > pathNameMaxSize) {
//            System.out.println("4");
//            return false;
//        }

        // 경로 유효성 확인
        // 1) '/'로 시작
        // 2) 숫자,알파벳,'_','-' 허용
        // 3) 마지막 '/' 허용 안함
        if (!Pattern.matches(ValidatorHelper.PATH_NAME_REG, value)) {
            System.out.println("5");
            return false;
        }

        return true;
    }

    @Override
    public void initialize(PathValid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }
}
