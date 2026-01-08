/*
 *
 *  * Copyright (c) 2025 Evolveum and contributors
 *  *
 *  * Licensed under the EUPL-1.2 or later.
 *
 */

package com.evolveum.validation.validator;

import com.evolveum.concepts.ValidationLog;
import com.evolveum.midpoint.prism.ParsingContext;
import com.evolveum.midpoint.prism.PrismContext;
import com.evolveum.midpoint.prism.path.ItemPath;
import com.evolveum.midpoint.prism.xnode.RootXNode;
import com.evolveum.midpoint.util.exception.SchemaException;
import com.evolveum.validation.common.SupportedLanguage;
import com.evolveum.validation.service.PrismContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import java.util.List;

public class CodeValidatorImpl implements CodeValidator {

    private static final Logger logger = LoggerFactory.getLogger(CodeValidatorImpl.class);
    private final com.evolveum.validation.validator.ValidationParams validationParams;

    public CodeValidatorImpl(ValidationParams validationParams) {
        this.validationParams = validationParams;
    }

    @Override
    public List<ValidationLog> validate(String rawObject, SupportedLanguage language) throws Exception {
        PrismContext prismContext = new PrismContextService().getPrismContext();
        ParsingContext parsingCtx = prismContext.createParsingContextForCompatibilityMode().validation();

        if (rawObject == null) {
            throw new Exception("Body input is empty.");
        }

        if (!SupportedLanguage.isSupported(language.getValue())) {
            throw new Exception("%s language is not supported.".formatted(language));
        }

        try {
            RootXNode root = prismContext.parserFor(rawObject)
                    .language(language.getValue())
                    .context(parsingCtx)
                    .parseToXNode();

            if (validationParams.objectType() != null) {
                QName typeQName = prismContext.getSchemaRegistry().qualifyTypeName(new QName(validationParams.objectType()));
                var complexTypeDefinition = prismContext.getSchemaRegistry().findComplexTypeDefinitionByType(typeQName);

                if (complexTypeDefinition == null) {
                    throw new Exception("Complex type definition is null.");
                } else {
                    var containerDefinition = prismContext.definitionFactory().newContainerDefinition(new QName("value"), complexTypeDefinition);

                    if (validationParams.itemPath() != null) {
                        ItemPath itemPath = prismContext.itemPathParser().asItemPath(validationParams.itemPath());
                        var itemDefinition = containerDefinition.findItemDefinition(itemPath);
                        prismContext.parserFor(root).context(parsingCtx).definition(itemDefinition).parseItem();
                    } else {
                        prismContext.parserFor(root).context(parsingCtx).definition(containerDefinition).parseItem();
                    }
                }
            } else {
                prismContext.parserFor(root).context(parsingCtx).parse();
            }
        } catch (SchemaException schemaException) {
            logger.error("Schema exception", schemaException);
        }

        return parsingCtx.getValidationLogs();
    }
}
