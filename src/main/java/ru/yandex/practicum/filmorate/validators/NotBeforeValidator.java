package ru.yandex.practicum.filmorate.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class NotBeforeValidator implements ConstraintValidator<NotBefore, LocalDate> {
    private LocalDate minDate;

    @Override
    public void initialize(NotBefore constraintAnnotation) {
        this.minDate = LocalDate.parse(
                constraintAnnotation.value(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd")
        );
    }

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        return !date.isBefore(minDate);
    }
}
