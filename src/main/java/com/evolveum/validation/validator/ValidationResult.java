/*
 *
 * Copyright (c) 2025 Evolveum and contributors
 *
 * Licensed under the EUPL-1.2 or later.
 *
 */

package com.evolveum.validation.validator;

import com.evolveum.concepts.ValidationLog;

import java.util.List;

public record ValidationResult(List<ValidationLog> validationFailures) {
}
