/*
 *
 *  * Copyright (c) 2025 Evolveum and contributors
 *  *
 *  * Licensed under the EUPL-1.2 or later.
 *
 */

package com.evolveum.validation.converter;

import com.evolveum.validation.common.SupportedLanguage;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by Dominik.
 */
@CommandLine.Command(
        name = "convert",
        description = ""
)
public class ConverterCommand implements Runnable {

    @CommandLine.Option(
            names = "--file",
            description = "Path to a file to convert"
    )
    private Path filePath;

    @CommandLine.Option(
            names = "--snippet",
            description = "Conversion code snippet"
    )
    private String snippet;

    @CommandLine.Option(
            names = "--targetLang",
            description = "Target language (xml, json, yaml)"
    )
    private String targetLanguage;
    private SupportedLanguage contentType;

    @Override
    public void run() {
        String content = "";
        SupportedLanguage contentType = null;

//        if (filePath != null && snippet == null) {
//            try {
//                content = readFileContent(filePath);
//                Optional<SupportedLanguage> foundSupportedLanguage = SupportedLanguage.fromValue(detectFileContentType(filePath));
//
//                if (foundSupportedLanguage.isPresent()) {
//                    contentType = foundSupportedLanguage.get();
//                }
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        } else if (snippet != null && type != null && filePath == null) {
//            content = snippet;
//            Optional<SupportedLanguage> foundSupportedLanguage = SupportedLanguage.fromValue(type);
//
//            if (foundSupportedLanguage.isPresent()) {
//                contentType = foundSupportedLanguage.get();
//            }
//        } else {
//            System.out.println("You must provide either --file or --snippet");
//            return;
//        }


//        SupportedLanguage language = SupportedLanguage.XML;
//
//        RootXNodeImpl root = (RootXNodeImpl) prismContext.parserFor(codeSnippet)
//                .language(language)
//                .context(parsingCtx)
//                .parseToXNode();
    }

    private String readFileContent(Path path) throws IOException {
        return Files.readString(path);
    }

    private String detectFileContentType(Path path) throws IOException {
        String type = Files.probeContentType(path);
        return (type != null) ? type : "unknown/unknown";
    }
}