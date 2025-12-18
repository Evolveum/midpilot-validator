package com.evolveum.validation.validator;

import com.evolveum.concepts.ValidationLog;

import java.util.List;

public record ValidationResult(List<ValidationLog> validationFailures) {
}
