package com.example.lab1.presentation.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class CosmicWordCheckValidator
        implements ConstraintValidator<CosmicWordCheck, String> {

    private static final Set<String> COSMIC_TERMS = Set.of(
            "star",
            "galaxy",
            "comet",
            "cosmic",
            "space",
            "nebula",
            "planet",
            "moon",
            "asteroid",
            "orbit",
            "meteor",
            "universe"
    );

    @Override
    public boolean isValid(
            String value,
            ConstraintValidatorContext context
    ) {
        if (value == null || value.isBlank()) {
            return true;
        }

        String normalizedValue = value.toLowerCase();

        return COSMIC_TERMS.stream()
                .anyMatch(normalizedValue::contains);
    }
}