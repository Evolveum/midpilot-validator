/*
 *
 * Copyright (c) 2025 Evolveum and contributors
 *
 * Licensed under the EUPL-1.2 or later.
 *
 */

package com.evolveum.validation.command;

import com.evolveum.concepts.ValidationLog;
import com.evolveum.validation.common.SupportedLanguage;
import com.evolveum.validation.converter.Converter;
import com.evolveum.validation.converter.ConverterParams;
import com.evolveum.validation.util.LanguageUtils;
import com.evolveum.validation.validator.ValidationParams;
import com.evolveum.validation.validator.ValidatorProvider;
import com.evolveum.validation.validator.ValidatorProviderImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
public class AppCommand implements CommandLineRunner {

    public final Command VALIDATE_COMMAND = new Command("validate", """
            validate Midpoint object \n \t <file-path-of-object> -f : validate object from file
            \n \t "<snippet-object>" : validate snippet from midpoint object
            \n \t -midpoint-samples or shortcut -ms : validate all objects in midpoint-samples repository
            """);
    public final Command CONVERT_COMMAND = new Command("convert", """
            convert Midpoint object \n \t "<snippet-object>" <target-language> : convert to target language snippet from midpoint object
            """);

    @Override
    public void run(String... args) throws Exception {
        if (args.length == 0) return;

        if (args[0].equals(VALIDATE_COMMAND.name())) {
            if (args.length == 2) {
                 if (args[1].equals("-ms") || args[1].equals("-midpoint-samples")) {
                     processMidpointSamples();
                } else {
                     String code = args[1];
                     SupportedLanguage language = LanguageUtils.detectLanguage(code);
                     validate(new ValidationParams(null, null), code, language);
                 }
            } else if (args.length == 3) {
                if (args[2].equals("-f")) {
                    Path path = Path.of(args[1]);
                    SupportedLanguage supportedLanguage = getLangByPath(path);

                    if (supportedLanguage == null) {
                        throw new IllegalArgumentException("Unsupported language");
                    }

                    validate(new ValidationParams(null, null), Files.readString(path), getLangByPath(path));
                }
            } else {
                displayCommandList();
                throw new IllegalArgumentException("Bad argument input");
            }
        } else if (args[0].equals(CONVERT_COMMAND.name())) {
            if (args.length == 3) {
                String code = args[1];
                convert(new ConverterParams(args[2]), code, LanguageUtils.detectLanguage(code));
            } else {
                displayCommandList();
                throw new IllegalArgumentException("Bad argument input");
            }
        } else {
            System.out.println("Unknown command .\n Supported commands: \n ");
            displayCommandList();
        }
    }

    private void validate(ValidationParams params, String code, SupportedLanguage contentType) throws Exception {
        ValidatorProvider<ValidationParams> validatorProvider = new ValidatorProviderImpl();
        List<ValidationLog> logs = validatorProvider.getValidator(params).validate(code, contentType);

        if (logs.isEmpty()) {
            System.out.println("Object is correct");
        } else {
            for (ValidationLog log : logs) {
                System.err.println(log.validationLogType().toString() + " " + log.location().toString() + " " + log.message() + "\n");
            }
        }
        System.out.println("\n\n\n");
    }

    private void convert(ConverterParams params, String code, SupportedLanguage contentType) {
        Converter convertor = new Converter(params.targetLanguage());
        String response = convertor.convert(code, contentType);
        System.out.println(response);
    }

    private void displayCommandList() {
        System.out.println(VALIDATE_COMMAND.name + " : " + VALIDATE_COMMAND.description);
        System.out.println(CONVERT_COMMAND.name + " : " + CONVERT_COMMAND.description);
    }

    private void processMidpointSamples() {
        Path dir = Path.of("midpoint-samples");

        if (!Files.exists(dir)) {
            throw new IllegalStateException("Directory does not exist: " + dir.toAbsolutePath());
        }

        try (Stream<Path> paths = Files.walk(dir)) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        System.out.println("File: " + path.getFileName());
                        try {
                            String content = Files.readString(path);
                            SupportedLanguage language = getLangByPath(path);
                            if (language != null) {
                                validate(new ValidationParams(null, null), content, language);
                            } else {
                                System.out.println("Unsupported language");
                            }
                        } catch (IOException e) {
                            System.out.println("Failed to read " + path + " -> " + e.getLocalizedMessage());
                        } catch (Exception e) {
                            System.out.println("Exception " + path + " -> " + e.getLocalizedMessage());
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException("Failed to scan target/midpoint-samples", e);
        }
    }

    private SupportedLanguage getLangByPath(Path path) {
        String fileName = path.getFileName().toString();
        int dot = fileName.lastIndexOf('.');
        if (dot > 0) {
            Optional<SupportedLanguage> supportedLanguage = SupportedLanguage.fromValue(fileName.substring(dot + 1));
            return supportedLanguage.orElse(null);
        }

        return null;
    }

    public record Command(String name, String description) {}
}
