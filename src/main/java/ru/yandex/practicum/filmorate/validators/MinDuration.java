package ru.yandex.practicum.filmorate.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MinDurationValidator.class)
public @interface MinDuration {
    String message() default "Длительность должна быть не менее {value}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    long value();
}
