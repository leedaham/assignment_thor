package me.hamtom.thor.directory.controller.helper;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@NotBlank(message = "pathName 값은 필수 값입니다.")
@Size(min = 2, max = 4096, message = "pathName 값은 최소 2자리, 최대 4096자리의 문자열입니다.")
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PathValidator.class)
public @interface PathValid {
    String message() default "Custom validation error";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
