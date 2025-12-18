/*
 * Copyright (c) 2025 Evolveum and contributors
 *
 * Licensed under the EUPL-1.2 or later.
 */
package com.evolveum.validation.validator.groovy;

import static org.testng.Assert.assertTrue;

import java.util.List;

import com.evolveum.concepts.ValidationLog;
import org.testng.annotations.Test;

public class GroovyCodeValidatorImplTest {

    @Test(enabled = false)
    void scriptContainsKnownMethodClosure_validateIsCalled_validationShouldPass() {
        final String script = """
                someMethod("User") {
                    println concat("X")
                }
                """;

        final GroovyValidator validator = new GroovyValidator(TestingBaseScript.class);
        final List<ValidationLog> errors = validator.validate(script, null);

        assertTrue(errors.isEmpty(), "Unexpected errors: " + errors);

    }
}