package me.hamtom.thor.directory.controller.validate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//@NotBlank(message = "pathName 값은 필수 값입니다.")
//@Size(min = 2, max = 4096, message = "pathName 값은 최소 2자리, 최대 4096자리의 문자열입니다.")
//@Pattern(regexp = ValidatorHelper.NO_BLANK_REG, message = "pathName 값은 빈 칸을 가질 수 없습니다.")
//@Pattern(regexp = ValidatorHelper.PATH_NAME_REG, message = "pathName 값은 '/'로 시작하며, 디렉토리 명은 영어 대/소문자, 숫자, '_', '-'를 허용합니다.")
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PathValidator.class)
public @interface PathValid {
    String message() default "PathValid error";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
