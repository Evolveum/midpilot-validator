/*
 * Copyright (c) 2026 Evolveum and contributors
 *
 * Licensed under the EUPL-1.2 or later.
 *
 */

package com.evolveum.validation.module.validator.mel;

import com.evolveum.concepts.ValidationLog;
import com.evolveum.midpoint.prism.util.JavaTypeConverter;
import com.evolveum.validation.common.SupportedLanguage;
import com.evolveum.validation.module.validator.CodeValidator;
import com.evolveum.validation.module.validator.ValidationResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/validate", consumes = "application/x-mel")
public class MelValidationController {

    private static final Logger LOG = LoggerFactory.getLogger(MelValidationController.class);

    private final MelValidatorProviderImpl validatorProvider;

    public MelValidationController(MelValidatorProviderImpl validatorProvider) {
        this.validatorProvider = validatorProvider;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> validate(@RequestBody String script, @Valid MelValidationParams configuration) {
        try {
            CodeValidator validator = validatorProvider.getValidator(SupportedLanguage.MEL);

            Class<?> varType = Class.forName(configuration.variableType());
            Object testValue = configuration.testValue() != null
                    ? JavaTypeConverter.convert(varType, configuration.testValue())
                    : null;

            List<ValidationLog> logs = validator.validate(
                    script,
                    configuration.variableName(),
                    varType,
                    testValue
            );

            return ResponseEntity.ok(new ValidationResponse(logs));
        } catch (Exception e) {
            LOG.error("Error during MEL validation", e);
            return ResponseEntity.internalServerError()
                    .body("Error during MEL validation: " + e.getClass().getName() + ": " + e.getMessage());
        }
    }
}
