/*
 *
 *  * Copyright (C) 2010-2025 Evolveum and contributors
 *  *
 *  * Licensed under the EUPL-1.2 or later.
 *
 */

package com.evolveum.validation.validator;

import com.evolveum.concepts.ValidationLog;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ValidationLogListSerializer extends JsonSerializer<List<ValidationLog>> {
    @Override
    public void serialize(List<ValidationLog> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartArray();

        for (ValidationLog log : value) {
            gen.writeStartObject();

            gen.writeStringField("validationLogType", log.validationLogType().getLabel());
            gen.writeStringField("location", log.location() != null ? log.location().toString() : null);
            gen.writeStringField("technicalMessage", log.technicalMessage().message().formatted(log.technicalMessage().arguments()));
            gen.writeStringField("technicalArguments", Arrays.toString(log.technicalMessage().arguments()));
            gen.writeObjectField("message", log.message());

            gen.writeEndObject();
        }

        gen.writeEndArray();
    }
}
