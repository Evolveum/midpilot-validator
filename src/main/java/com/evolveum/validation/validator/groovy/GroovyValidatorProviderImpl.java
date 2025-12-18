/*
 * Copyright (c) 2010-2018 Evolveum and contributors
 *
 * Licensed under the EUPL-1.2 or later.
 */
package com.evolveum.validation.validator.groovy;

import com.evolveum.validation.validator.ValidatorProvider;
import org.springframework.stereotype.Component;

import com.evolveum.validation.validator.CodeValidator;

@Component
final class GroovyValidatorProviderImpl implements ValidatorProvider<GroovyValidationParams> {

    @Override
    public CodeValidator getValidator(GroovyValidationParams validationParams) {
        return new GroovyValidator(validationParams.scriptBaseClass());
    }

}
