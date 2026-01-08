/*
 *
 *  * Copyright (c) 2025 Evolveum and contributors
 *  *
 *  * Licensed under the EUPL-1.2 or later.
 *
 */
package com.evolveum.validation.validator.groovy;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.lang.Script;

public abstract class TestingBaseScript extends Script {

    public TestingBaseScript someMethod(String name,
            @DelegatesTo(value = String.class, strategy = Closure.DELEGATE_FIRST) Closure<?> closure) {
        closure.setDelegate(name);
        closure.setResolveStrategy(Closure.DELEGATE_FIRST);
        closure.call();
        return this;
    }

}
