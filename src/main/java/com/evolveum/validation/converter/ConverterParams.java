/*
 *
 * Copyright (c) 2025 Evolveum and contributors
 *
 * Licensed under the EUPL-1.2 or later.
 *
 */

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
