/*
 * Copyright (c) 2025 Evolveum and contributors
 *
 * Licensed under the EUPL-1.2 or later.
 *
 */

package com.evolveum.validation.module.validator;

import org.springframework.stereotype.Component;


@Component
public class ValidatorProviderImpl implements ValidatorProvider<ValidationParams> {

    @Override
    public CodeValidator getValidator(ValidationParams params) {
        return new CodeValidatorImpl(params);
    }
}
