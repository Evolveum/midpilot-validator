/*
 *
 *  * Copyright (c) 2025 Evolveum and contributors
 *  *
 *  * Licensed under the EUPL-1.2 or later.
 *
 */

package com.evolveum.validation.validator.groovy;

import com.evolveum.concepts.SourceLocation;
import com.evolveum.concepts.TechnicalMessage;
import com.evolveum.concepts.ValidationLog;
import com.evolveum.concepts.ValidationLogType;
import com.evolveum.validation.common.SupportedLanguage;
import com.evolveum.validation.validator.CodeValidator;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import groovy.transform.CompileStatic;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;

final class GroovyValidator implements CodeValidator {

    private static final Logger LOG = LoggerFactory.getLogger(GroovyValidator.class);

    private final Class<? extends Script> baseScriptClass;

    GroovyValidator(Class<? extends Script> baseScriptClass) {
        this.baseScriptClass = baseScriptClass;
    }

    @Override
    public List<ValidationLog> validate(String script, SupportedLanguage language) {
        final CompilerConfiguration config = new CompilerConfiguration();
        config.setScriptBaseClass(this.baseScriptClass.getName());
        config.addCompilationCustomizers(new ASTTransformationCustomizer(CompileStatic.class));

        try {
            final GroovyShell shell = new GroovyShell(config);
            LOG.trace("Parsing script:\n{}", script);
            shell.parse(script);
        } catch (MultipleCompilationErrorsException e) {
            return e.getErrorCollector().getErrors().stream()
                    .map(m -> {
                        final StringWriter stringWriter = new StringWriter();
                        m.write(new PrintWriter(stringWriter));
                        return new ValidationLog(ValidationLogType.WARNING, ValidationLogType.Specification.UNKNOW, SourceLocation.unknown(), new TechnicalMessage(""), stringWriter.toString());
                    })
                    .toList();
        }
        return Collections.emptyList();
    }

}
