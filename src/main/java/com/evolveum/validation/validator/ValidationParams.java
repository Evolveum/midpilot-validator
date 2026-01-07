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
