package com.evolveum.validation.converter;

import jakarta.validation.constraints.NotNull;

/**
 * Created by Dominik.
 */
public record ConverterParams(
        @NotNull(message = "Target language is required.")
        String targetLanguage
) {
}
