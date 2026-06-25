/*
 * Copyright (c) 2025 Evolveum and contributors
 *
 * Licensed under the EUPL-1.2 or later.
 *
 */

package com.evolveum.validation.config;

import com.evolveum.midpoint.common.LocalizationService;
import com.evolveum.midpoint.common.LocalizationServiceImpl;
import com.evolveum.midpoint.prism.PrismContext;
import com.evolveum.midpoint.prism.crypto.Protector;
import com.evolveum.midpoint.prism.impl.crypto.KeyStoreBasedProtectorImpl;
import com.evolveum.midpoint.schema.MidPointPrismContextFactory;
import com.evolveum.midpoint.util.exception.SchemaException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PrismConfiguration {

    @Bean
    public Protector protector() {
        return new KeyStoreBasedProtectorImpl();
    }

    @Bean
    public PrismContext prismContext() throws SchemaException, java.io.IOException {
        MidPointPrismContextFactory factory = new MidPointPrismContextFactory();
        return factory.createPrismContext();
    }

    @Bean
    public LocalizationService localizationService() {
        LocalizationServiceImpl service = new LocalizationServiceImpl();
        service.init();
        return service;
    }
}
