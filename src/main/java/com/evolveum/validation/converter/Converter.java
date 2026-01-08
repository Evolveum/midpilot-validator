/*
 *
 *  * Copyright (c) 2025 Evolveum and contributors
 *  *
 *  * Licensed under the EUPL-1.2 or later.
 *
 */

package com.evolveum.validation.converter;

import com.evolveum.midpoint.prism.ParsingContext;
import com.evolveum.midpoint.prism.PrismContext;
import com.evolveum.midpoint.prism.impl.xnode.RootXNodeImpl;
import com.evolveum.validation.common.SupportedLanguage;
import com.evolveum.validation.service.PrismContextService;

/**
 * Created by Dominik.
 */
public class Converter {

    private final String targetLang;

    public Converter(String targetLang) {
        this.targetLang = targetLang;
    }

    public String convert(String codeSnippet, SupportedLanguage language) {
        try {
            PrismContext prismContext = new PrismContextService().getPrismContext();
            ParsingContext parsingCtx = prismContext.createParsingContextForCompatibilityMode();

            if (codeSnippet == null) {
                throw new Exception("Body input is empty.");
            }

            if (!SupportedLanguage.isSupported(language.getValue())) {
                throw new Exception("%s language is not supported.".formatted(language.getValue()));
            }

            RootXNodeImpl root = (RootXNodeImpl) prismContext.parserFor(codeSnippet)
                    .language(language.getValue())
                    .context(parsingCtx)
                    .parseToXNode();

            return prismContext.serializerFor(targetLang).serialize(root);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
