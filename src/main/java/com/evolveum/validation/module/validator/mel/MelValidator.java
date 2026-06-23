/*
 * Copyright (c) 2025 Evolveum and contributors
 *
 * Licensed under the EUPL-1.2 or later.
 *
 */

package com.evolveum.validation.module.validator.mel;

import com.evolveum.concepts.SourceLocation;
import com.evolveum.concepts.TechnicalMessage;
import com.evolveum.concepts.ValidationLog;
import com.evolveum.concepts.ValidationLogType;
import com.evolveum.midpoint.common.Clock;
import com.evolveum.midpoint.common.LocalizationService;
import com.evolveum.midpoint.model.common.expression.functions.BasicExpressionFunctions;
import com.evolveum.midpoint.model.common.expression.functions.FunctionLibraryBinding;
import com.evolveum.midpoint.model.common.expression.functions.FunctionLibraryUtil;
import com.evolveum.midpoint.model.common.expression.script.ScriptExpressionEvaluationContext;
import com.evolveum.midpoint.model.common.expression.script.mel.MelScriptEvaluator;
import com.evolveum.midpoint.prism.PrismContext;
import com.evolveum.midpoint.prism.crypto.Protector;
import com.evolveum.midpoint.schema.expression.TypedValue;
import com.evolveum.midpoint.schema.expression.VariablesMap;
import com.evolveum.midpoint.schema.result.OperationResult;
import com.evolveum.midpoint.xml.ns._public.common.common_3.ScriptExpressionEvaluatorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

public class MelValidator {

    private static final Logger LOG = LoggerFactory.getLogger(MelValidator.class);

    private final MelScriptEvaluator melScriptEvaluator;

    public MelValidator(PrismContext prismContext, Protector protector, LocalizationService localizationService) {
        FunctionLibraryBinding basicFunctionLibraryBinding =
                FunctionLibraryUtil.createBasicFunctionLibraryBinding(prismContext, protector, new Clock());

        this.melScriptEvaluator = new MelScriptEvaluator(
                prismContext,
                protector,
                localizationService,
                (BasicExpressionFunctions) basicFunctionLibraryBinding.getImplementation(),
                null
        );
    }

    public List<ValidationLog> validate(
            String script,
            String variableName,
            Class<?> variableType,
            Object testValue) {

        boolean hasTestData = variableName != null;
        String contextDescription = hasTestData ? "MEL validation with test data" : "MEL syntax validation";
        String operationName = hasTestData ? "mel-validation-with-test-data" : "mel-validation";

        try {
            var scriptBean = new ScriptExpressionEvaluatorType();
            scriptBean.setCode(script);
            scriptBean.setLanguage(MelScriptEvaluator.LANGUAGE_URL);

            VariablesMap variables = new VariablesMap();
            if (hasTestData) {
                variables.put(variableName, new TypedValue<>(testValue, variableType));
            }

            ScriptExpressionEvaluationContext context = new ScriptExpressionEvaluationContext();
            context.setScriptBean(scriptBean);
            context.setVariables(variables);
            context.setContextDescription(contextDescription);
            context.setResult(new OperationResult(operationName));
            context.setEvaluateNew(false);

            melScriptEvaluator.evaluate(context);

            return Collections.emptyList();
        } catch (Exception e) {
            LOG.error("MEL validation error: {}", contextDescription, e);
            return List.of(new ValidationLog(
                    ValidationLogType.ERROR,
                    ValidationLogType.Specification.UNKNOW,
                    SourceLocation.unknown(),
                    new TechnicalMessage(""),
                    e.getMessage()
            ));
        }
    }

}
