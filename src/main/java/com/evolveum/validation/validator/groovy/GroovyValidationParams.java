/*
 * Copyright (c) 2010-2018 Evolveum and contributors
 *
 * Licensed under the EUPL-1.2 or later.
 */
package com.evolveum.validation.validator.groovy;

import groovy.lang.Script;
import jakarta.validation.constraints.NotNull;

public record GroovyValidationParams(
        SnippetType type,

        @NotNull(message = "Script base class is required.")
        Class<? extends Script> scriptBaseClass) {

}
