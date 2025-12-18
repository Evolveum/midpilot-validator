/*
 *
 *  * Copyright (C) 2010-2025 Evolveum and contributors
 *  *
 *  * Licensed under the EUPL-1.2 or later.
 *
 */

package com.evolveum.validation.validator;

import com.evolveum.concepts.ValidationLog;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

public class ValidationResponse {

    @JsonSerialize(using = ValidationLogListSerializer.class)
    private List<ValidationLog> logs;

    public ValidationResponse(List<ValidationLog> logs) {
        this.logs = logs;
    }

    public List<ValidationLog> getLogs() {
        return logs;
    }
}