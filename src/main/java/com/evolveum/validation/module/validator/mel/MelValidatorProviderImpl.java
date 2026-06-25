/*
 * Copyright (c) 2026 Evolveum and contributors
 *
 * Licensed under the EUPL-1.2 or later.
 *
 */

package com.evolveum.validation.module.validator.mel;

import com.evolveum.midpoint.common.LocalizationService;
import com.evolveum.midpoint.prism.PrismContext;
import com.evolveum.midpoint.prism.crypto.Protector;
import com.evolveum.validation.common.SupportedLanguage;
import com.evolveum.validation.module.validator.CodeValidator;
import com.evolveum.validation.module.validator.ValidatorProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class MelValidatorProviderImpl implements ValidatorProvider<SupportedLanguage> {

    private final PrismContext prismContext;
    private final Protector protector;
    private final LocalizationService localizationService;

    @Autowired
    public MelValidatorProviderImpl(
            PrismContext prismContext,
            Protector protector,
            @Nullable LocalizationService localizationService) {
        this.prismContext = prismContext;
        this.protector = protector;
        this.localizationService = localizationService;
    }

    @Override
    public CodeValidator getValidator(SupportedLanguage language) {
        return new MelValidator(prismContext, protector, localizationService);
    }
}
