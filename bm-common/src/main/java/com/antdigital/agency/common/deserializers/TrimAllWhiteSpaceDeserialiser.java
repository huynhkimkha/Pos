package com.antdigital.agency.common.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class TrimAllWhiteSpaceDeserialiser extends StdDeserializer<String> {
    public TrimAllWhiteSpaceDeserialiser() {
        this(null);
    }

    public TrimAllWhiteSpaceDeserialiser(Class<?> vc) {
        super(vc);
    }

    @Override
    public String deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        return jp.getValueAsString().replaceAll("\\s+", "");
    }
}
