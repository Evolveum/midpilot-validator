/*
 *
 * Copyright (c) 2025 Evolveum and contributors
 *
 * Licensed under the EUPL-1.2 or later.
 *
 */

package com.evolveum.validation.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by Dominik.
 */
public class FileHelper {

    public static String readFileContent(Path path) throws IOException {
        return Files.readString(path);
    }

    public static String detectFileContentType(Path path) throws IOException {
        String type = Files.probeContentType(path);
        return (type != null) ? type : "unknown/unknown";
    }
}
