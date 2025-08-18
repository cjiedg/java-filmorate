package ru.yandex.practicum.filmorate.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.yandex.practicum.filmorate.validator.EqualOrAfterValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EqualOrAfterValidator.class)

public @interface EqualOrAfter {
    String message() default "Некорректная дата";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
