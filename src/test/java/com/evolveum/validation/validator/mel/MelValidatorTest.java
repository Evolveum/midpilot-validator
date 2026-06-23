/*
 * Copyright (c) 2025 Evolveum and contributors
 *
 * Licensed under the EUPL-1.2 or later.
 *
 */

package com.evolveum.validation.validator.mel;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.List;

import com.evolveum.concepts.ValidationLog;
import com.evolveum.midpoint.prism.PrismContext;
import com.evolveum.midpoint.schema.MidPointPrismContextFactory;
import com.evolveum.validation.module.validator.mel.MelValidator;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class MelValidatorTest {

    private PrismContext prismContext;

    @BeforeClass
    public void setup() throws Exception {
        MidPointPrismContextFactory factory = new MidPointPrismContextFactory();
        this.prismContext = factory.createPrismContext();
    }

    @Test
    void melExpressionIsValid_validateWithTestData_validationShouldPass() {
        final String melCode = "input.replace('-', '')";

        final MelValidator validator = new MelValidator(
                prismContext,
                null,
                null
        );
        final List<ValidationLog> errors = validator.validate(
                melCode,
                "input",
                String.class,
                "1-2-3"
        );

        assertTrue(errors.isEmpty(), "Unexpected errors: " + errors);
    }

    @Test
    void melExpressionIsNotValid_validateWithTestData_validationShouldFail() {
        final String melCode = "input.replce('-', '')"; // typo: replce instead of replace

        final MelValidator validator = new MelValidator(
                prismContext,
                null,
                null
        );
        final List<ValidationLog> errors = validator.validate(
                melCode,
                "input",
                String.class,
                "1-2-3"
        );

        assertFalse(errors.isEmpty(), "Expected validation errors");
    }
}
