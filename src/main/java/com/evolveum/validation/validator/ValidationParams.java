package com.evolveum.validation.validator;

import jakarta.servlet.http.HttpServletRequest;

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
