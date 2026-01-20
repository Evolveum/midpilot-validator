/*
 *
 * Copyright (c) 2025 Evolveum and contributors
 *
 * Licensed under the EUPL-1.2 or later.
 *
 */

package com.evolveum.validation.converter;

import com.evolveum.validation.common.SupportedLanguage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Dominik.
 */
@RestController
@RequestMapping(path = "/convert")
public class ConverterController {

    @PostMapping(consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> handleXml(@RequestBody String codeSnippet, ConverterParams params) {
        return convert(codeSnippet, params, SupportedLanguage.XML);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> handleJson(@RequestBody String codeSnippet, ConverterParams params) {
        return convert(codeSnippet, params, SupportedLanguage.JSON);
    }

    @PostMapping(consumes = MediaType.APPLICATION_YAML_VALUE)
    public ResponseEntity<?> handleYaml(@RequestBody String codeSnippet, ConverterParams params) {
        return convert(codeSnippet, params, SupportedLanguage.YAML);
    }

    public ResponseEntity<?> convert(String codeSnippet, ConverterParams params, SupportedLanguage contentType) {
        try {
            Converter convertor = new Converter(params.targetLanguage());
            SupportedLanguage lang = SupportedLanguage
                    .fromValue(params.targetLanguage())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Unsupported language: " + params.targetLanguage()
                    ));

            return ResponseEntity.ok()
                    .contentType(switch (lang) {
                        case XML  -> MediaType.APPLICATION_XML;
                        case JSON -> MediaType.APPLICATION_JSON;
                        case YAML -> MediaType.APPLICATION_YAML;
                    }).body(convertor.convert(codeSnippet, contentType));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getLocalizedMessage());
        }
    }
}
