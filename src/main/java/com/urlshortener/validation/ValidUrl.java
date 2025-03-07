package com.urlshortener.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Custom annotation for validating URLs.
 */
@Documented
@Constraint(validatedBy = UrlValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUrl {
    /**
     * Error message to be returned if the URL is invalid.
     *
     * @return the error message
     */
    String message() default "Invalid URL format";

    /**
     * Groups for categorizing constraints.
     *
     * @return the groups
     */
    Class<?>[] groups() default {};

    /**
     * Payload for carrying metadata information.
     *
     * @return the payload
     */
    Class<? extends Payload>[] payload() default {};
}
