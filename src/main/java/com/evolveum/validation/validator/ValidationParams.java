/*
 *
 *  * Copyright (c) 2025 Evolveum and contributors
 *  *
 *  * Licensed under the EUPL-1.2 or later.
 *
 */

package com.evolveum.validation.validator;

import javax.annotation.Nullable;

/**
 * Created by Dominik.
 */
public record ValidationParams(
        @Nullable
        String objectType,
        @Nullable
        String itemPath) {
}
