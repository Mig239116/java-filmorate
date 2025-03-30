package ru.yandex.practicum.filmorate.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotBeforeValidator.class)
public @interface NotBefore {
    String message() default "Дата релиза не раньше {value}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String value();
}
