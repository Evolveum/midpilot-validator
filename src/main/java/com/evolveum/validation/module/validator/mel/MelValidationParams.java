/*
 * Copyright (c) 2026 Evolveum and contributors
 *
 * Licensed under the EUPL-1.2 or later.
 *
 */

package com.evolveum.validation.module.validator.mel;

import jakarta.validation.constraints.NotNull;

public record MelValidationParams(
        @NotNull(message = "Variable name is required.")
        String variableName,

        @NotNull(message = "Variable type is required.")
        String variableType,

        String testValue) {

}
