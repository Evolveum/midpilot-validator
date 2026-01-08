/*
 *
 * Copyright (c) 2025 Evolveum and contributors
 *
 * Licensed under the EUPL-1.2 or later.
 *
 */

package com.evolveum.validation.validator;

import com.evolveum.concepts.ValidationLog;
import com.evolveum.validation.common.SupportedLanguage;
import com.evolveum.validation.util.FileHelper;
import com.evolveum.validation.validator.ValidationParams;
import com.evolveum.validation.validator.ValidatorProvider;
import com.evolveum.validation.validator.ValidatorProviderImpl;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;


/**
 * Created by Dominik.
 */
@Command(
        name = "validate",
        description = "Validate (xml, json, yaml) object using PRISM parser"
)
public class ValidatorCommand implements Runnable {

    @Option(
            names = {"-f", "--file"},
            description = "Path to a file to validate"
    )
    private Path filePath;

    @Option(
            names = "--snippet",
            description = "Inline text snippet to validate"
    )
    private String snippet;

    @Option(
            names = "--contentType",
            description = "Inline content type. Can be (xml, json, yaml)"
    )
    private String type;

    @Override
    public void run() {
        String content = "";
        SupportedLanguage contentType = null;

        if (filePath != null && snippet == null) {

            try {
                content = FileHelper.readFileContent(filePath);

                System.out.println("Success to read file : " + content);

                Optional<SupportedLanguage> foundSupportedLanguage = SupportedLanguage.fromValue(FileHelper.detectFileContentType(filePath));

                if (foundSupportedLanguage.isPresent()) {
                    contentType = foundSupportedLanguage.get();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (snippet != null && type != null && filePath == null) {
            content = snippet;
            Optional<SupportedLanguage> foundSupportedLanguage = SupportedLanguage.fromValue(type);

            if (foundSupportedLanguage.isPresent()) {
                contentType = foundSupportedLanguage.get();
            }
        } else {
            System.out.println("You must provide either --file or --snippet");
            return;
        }

        ValidatorProvider<ValidationParams> validatorProvider = new ValidatorProviderImpl();
        ValidationParams params = new ValidationParams(null, null);
        try {
            List<ValidationLog> logs = validatorProvider.getValidator(params).validate(content, contentType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}