package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.annotation.EqualOrAfter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static ru.yandex.practicum.filmorate.model.Film.EARLIEST_RELEASE_DATE;

public class EqualOrAfterValidator implements ConstraintValidator<EqualOrAfter, LocalDate> {

    @Override
    public void initialize(EqualOrAfter constraintAnnotation) {
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        String formattedDate = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.of("ru")).format(EARLIEST_RELEASE_DATE);
        if (value == null) {
            return true;
        }

        if (value.isBefore(EARLIEST_RELEASE_DATE)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Дата релиза не может быть раньше " + formattedDate
            ).addConstraintViolation();
            return false;
        }
        return true;
    }

}
















