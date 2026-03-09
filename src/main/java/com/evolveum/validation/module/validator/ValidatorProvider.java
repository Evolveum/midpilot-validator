/*
 * Copyright (c) 2025 Evolveum and contributors
 *
 * Licensed under the EUPL-1.2 or later.
 *
 */

package com.evolveum.validation.module.validator;

public interface ValidatorProvider<P> {

    CodeValidator getValidator(P validationParams);
}
