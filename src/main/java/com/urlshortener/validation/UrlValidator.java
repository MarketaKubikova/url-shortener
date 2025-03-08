package com.urlshortener.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * Validator class for validating URLs using the @ValidUrl annotation.
 */
public class UrlValidator implements ConstraintValidator<ValidUrl, String> {
    /**
     * Regular expression pattern for validating URLs.
     */
    private static final Pattern URL_PATTERN = Pattern.compile(
            "^(https?://)?(www\\.)?([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}(/.*)?$"
    );

    /**
     * Initializes the validator. No initialization needed in this case.
     *
     * @param constraintAnnotation the annotation instance for a given constraint declaration
     */
    @Override
    public void initialize(ValidUrl constraintAnnotation) {
        // No initialization needed
    }

    /**
     * Validates the given URL.
     *
     * @param url     the URL to validate
     * @param context context in which the constraint is evaluated
     * @return true if the URL is valid, false otherwise
     */
    @Override
    public boolean isValid(String url, ConstraintValidatorContext context) {
        return url != null && URL_PATTERN.matcher(url).matches();
    }
}
