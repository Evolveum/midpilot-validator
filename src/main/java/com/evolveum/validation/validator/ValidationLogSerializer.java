/*
 *
 * Copyright (c) 2025 Evolveum and contributors
 *
 * Licensed under the EUPL-1.2 or later.
 *
 */

package com.evolveum.validation.validator;

import com.evolveum.concepts.Argument;
import com.evolveum.concepts.ValidationLog;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ValidationLogSerializer extends JsonSerializer<List<ValidationLog>> {
    @Override
    public void serialize(List<ValidationLog> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartArray();

        for (ValidationLog log : value) {
            // start log object
            gen.writeStartObject();
            gen.writeObjectField("type", log.validationLogType());
            gen.writeObjectField("specification", log.specification());
            gen.writeObjectField("location", log.location());
            gen.writeStringField("message", log.message());
            // start technical message object
            gen.writeObjectFieldStart("technicalMessage");
            gen.writeStringField("message", log.technicalMessage().message());
            gen.writeArrayFieldStart("arguments");
            for (Argument argument : log.technicalMessage().arguments()) {
                gen.writeStartObject();

                if (argument.type().equals(Argument.ArgumentType.DEFINITION_LIST)) {
                    gen.writeArrayFieldStart("definitions");
                    if (argument.value() instanceof ArrayList<?> arrayList) {
                        for (Object def : arrayList) {
                            gen.writeString(def.toString());
                        }
                    }
                    gen.writeEndArray();
                } else if (argument.type().equals(Argument.ArgumentType.DEFINITION)) {
                    gen.writeStringField("value", argument.value().toString());
                } else {
                    gen.writeObjectField("value", argument.value());
                }

                gen.writeObjectField("type", argument.type());
                gen.writeEndObject();
            }
            gen.writeEndArray();
            // end technical message object
            gen.writeEndObject();
            // end log object
            gen.writeEndObject();
        }

        gen.writeEndArray();
    }
}
