/*
 * Copyright (c) 2025 Evolveum and contributors
 *
 * Licensed under the EUPL-1.2 or later.
 *
 */

package com.evolveum.validation.module.validator;

import com.evolveum.concepts.ValidationLog;
import com.evolveum.validation.common.SupportedLanguage;

import java.util.List;

public interface CodeValidator {

    /**
     * Validate provided code.
     *
     * Depending on the implementation it may do these (but not only) validations:
     *
     * * Syntax validation
     * * Semantics validation (e.g. validation against schema)
     *
     * If the validation passes without any errors, empty {@code List} is returned. Otherwise, returned {@code
     * List} will contain the messages describing the validation problems.
     *
     * @param code the code snippet, which should be validated.
     * @param language input code language
     *
     * @return Validation failure description if the validation fails, empty {@link List} otherwise.
     */
    List<ValidationLog> validate(String code, SupportedLanguage language) throws Exception;

    /**
     * Validate script with variable context.
     *
     * @param script the code snippet to validate
     * @param variableName the name of the variable available in the context
     * @param variableType the type of the variable
     * @param testValue the test value for the variable (can be null)
     *
     * @return Validation failure description if the validation fails, empty {@link List} otherwise.
     */
    List<ValidationLog> validate(String script, String variableName, Class<?> variableType, Object testValue);

    /**
     * Evaluate script with variable context and return the transformed value or error.
     *
     * @param script the code snippet to evaluate
     * @param variableName the name of the variable available in the context
     * @param variableType the type of the variable
     * @param testValue the test value for the variable (can be null)
     *
     * @return {@link EvaluationResponse} containing either the transformed value or error details.
     */
    EvaluationResponse evaluateWithResult(String script, String variableName, Class<?> variableType, Object testValue);

}
