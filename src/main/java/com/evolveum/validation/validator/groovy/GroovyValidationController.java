/*
 * Copyright (c) 2010-2018 Evolveum and contributors
 *
 * Licensed under the EUPL-1.2 or later.
 */
package com.evolveum.validation.validator.groovy;

import com.evolveum.validation.validator.ValidatorProvider;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.evolveum.validation.validator.ValidationResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/validate", consumes = "text/x-groovy")
public class GroovyValidationController {

    private final ValidatorProvider<GroovyValidationParams> validatorProvider;

    public GroovyValidationController(ValidatorProvider<GroovyValidationParams> validatorProvider) {
        this.validatorProvider = validatorProvider;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ValidationResult> validate(String codeSnippet, @Valid GroovyValidationParams configuration) {
        System.out.println(configuration);

        return ResponseEntity.ok(new ValidationResult(null));
    }
}
