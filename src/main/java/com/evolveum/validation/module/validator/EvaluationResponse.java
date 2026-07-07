/*
 * Copyright (c) 2026 Evolveum and contributors
 *
 * Licensed under the EUPL-1.2 or later.
 *
 */

package com.evolveum.validation.module.validator;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EvaluationResponse(
        Object transformedValue,
        String error,
        String errorType
) {
    public static EvaluationResponse success(Object transformedValue) {
        return new EvaluationResponse(transformedValue, null, null);
    }

    public static EvaluationResponse error(String error, String errorType) {
        return new EvaluationResponse(null, error, errorType);
    }

    public boolean isSuccess() {
        return error == null;
    }
}
