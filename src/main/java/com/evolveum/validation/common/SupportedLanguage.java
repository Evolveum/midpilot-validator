/*
 *
 *  * Copyright (c) 2025 Evolveum and contributors
 *  *
 *  * Licensed under the EUPL-1.2 or later.
 *
 */

package com.evolveum.validation.common;

import com.evolveum.midpoint.prism.PrismContext;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by Dominik.
 */
public enum SupportedLanguage {

    XML(PrismContext.LANG_XML),
    JSON(PrismContext.LANG_JSON),
    YAML(PrismContext.LANG_YAML);

    private final String value;

    SupportedLanguage(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Optional<SupportedLanguage> fromValue(String value) {
        return Arrays.stream(values())
                .filter(lang -> lang.value.equalsIgnoreCase(value))
                .findFirst();
    }

    public static boolean isSupported(String value) {
        return fromValue(value).isPresent();
    }
}
