/*
 *
 *  * Copyright (c) 2025 Evolveum and contributors
 *  *
 *  * Licensed under the EUPL-1.2 or later.
 *
 */

package com.evolveum.validation.validator;

import com.evolveum.concepts.ValidationLog;
import com.evolveum.validation.common.SupportedLanguage;
import com.evolveum.validation.validator.ValidationParams;
import com.evolveum.validation.validator.ValidationResponse;
import com.evolveum.validation.validator.ValidatorProvider;
import com.evolveum.validation.validator.ValidatorProviderImpl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by Dominik.
 */
@RestController
@RequestMapping(path = "/validate")
public class ValidatorController {

    @PostMapping(consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> handleXml(@RequestBody String codeSnippet, ValidationParams params) {
        return validate(codeSnippet, params, SupportedLanguage.XML);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> handleJson(@RequestBody String codeSnippet, ValidationParams params) {
        return validate(codeSnippet, params, SupportedLanguage.JSON);
    }

    @PostMapping(consumes = MediaType.APPLICATION_YAML_VALUE)
    public ResponseEntity<?> handleYaml(@RequestBody String codeSnippet, ValidationParams params) {
        return validate(codeSnippet, params, SupportedLanguage.YAML);
    }

    private ResponseEntity<?> validate(@RequestBody String codeSnippet, ValidationParams params, SupportedLanguage contentType) {
        try {
            ValidatorProvider<ValidationParams> validatorProvider = new ValidatorProviderImpl();
            List<ValidationLog> logs = validatorProvider.getValidator(params).validate(codeSnippet, contentType);
            return ResponseEntity.ok(new ValidationResponse(logs));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e);
        }
    }
}
