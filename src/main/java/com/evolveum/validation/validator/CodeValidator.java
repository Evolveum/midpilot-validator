package com.evolveum.validation.validator;

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
    List<ValidationLog> validate(String code, SupportedLanguage language);

    /**
     * Compose two validators, by combining their results.
     *
     * @param nextValidator the next validator which should be composed with the current one.
     * @return the code validator, whose validation result will be combination of validation results of both composed
     * validators.
     */
    default CodeValidator compose(CodeValidator nextValidator) {
        return (code, lang) -> {
            final List<ValidationLog> validationResults = this.validate(code, lang);
            validationResults.addAll(nextValidator.validate(code, lang));
            return validationResults;
        };
    }
}
