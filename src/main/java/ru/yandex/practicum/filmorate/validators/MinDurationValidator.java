package ru.yandex.practicum.filmorate.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Duration;
import java.time.LocalDate;

public class MinDurationValidator implements ConstraintValidator<MinDuration, Duration> {
    private long minSeconds;

    @Override
    public void initialize(MinDuration constraintAnnotation) {
        this.minSeconds = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Duration duration, ConstraintValidatorContext context) {
        return duration.getSeconds() >= minSeconds;
    }
}
