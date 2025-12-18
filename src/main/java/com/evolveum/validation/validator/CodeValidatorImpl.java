/*
 * Copyright (c) 2010-2018 Evolveum and contributors
 *
 * Licensed under the EUPL-1.2 or later.
 */
package com.evolveum.validation.validator;

import com.evolveum.concepts.SourceLocation;
import com.evolveum.concepts.TechnicalMessage;
import com.evolveum.concepts.ValidationLog;
import com.evolveum.concepts.ValidationLogType;
import com.evolveum.midpoint.prism.ParsingContext;
import com.evolveum.midpoint.prism.PrismContext;
import com.evolveum.midpoint.prism.PrismObjectDefinition;
import com.evolveum.midpoint.prism.impl.xnode.RootXNodeImpl;
import com.evolveum.midpoint.prism.path.ItemPath;
import com.evolveum.midpoint.util.exception.ValidationException;
import com.evolveum.validation.service.PrismContextService;
import com.evolveum.validation.common.SupportedLanguage;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

public class CodeValidatorImpl implements CodeValidator {

    private final ValidationParams validationParams;

    public CodeValidatorImpl(ValidationParams validationParams) {
        this.validationParams = validationParams;
    }

    @Override
    public List<ValidationLog> validate(String rawObject, SupportedLanguage language) {
        List<ValidationLog> logHandler = new ArrayList<>();

        try {
            PrismContext prismContext = new PrismContextService().getPrismContext();
            ParsingContext parsingCtx = prismContext.createParsingContextForCompatibilityMode().validation();

            if (rawObject == null) {
                throw new Exception("Body input is empty.");
            }

            if (!SupportedLanguage.isSupported(language.getValue())) {
                throw new Exception("%s language is not supported.".formatted(language));
            }

            RootXNodeImpl root = (RootXNodeImpl) prismContext.parserFor(rawObject)
                    .language(language.getValue())
                    .context(parsingCtx)
                    .parseToXNode();

            if (validationParams.objectType() != null) {
                QName typeQName = prismContext.getSchemaRegistry().qualifyTypeName(new QName(validationParams.objectType()));
                PrismObjectDefinition<?> objectDefinition = prismContext.getSchemaRegistry().findObjectDefinitionByType(typeQName);

                if (validationParams.itemPath() != null) {
                    ItemPath itemPath = prismContext.itemPathParser().asItemPath(validationParams.itemPath());
                    objectDefinition = objectDefinition.findItemDefinition(itemPath);
                }
                // parsing with specified item definition
                prismContext.parserFor(root).context(parsingCtx).definition(objectDefinition).parse();
            } else {
                prismContext.parserFor(root).context(parsingCtx).parse();
            }

            logHandler.addAll(parsingCtx.getValidationLogs());

        } catch (ValidationException e) {
            logHandler.addAll(e.getValidationLogs());
        } catch (Exception e) {
            logHandler.add(new ValidationLog(
                    ValidationLogType.ERROR,
                    SourceLocation.unknown(),
                    new TechnicalMessage(e.getLocalizedMessage()),
                    e.getMessage())
            );
        }

        return logHandler;
    }
}
