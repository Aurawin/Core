package com.aurawin.core.json.Serializers;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.Instant;

public class InstantConverter implements JsonSerializer<Instant>, JsonDeserializer<Instant> {
    @Override
    public JsonElement serialize(Instant src, Type srcType, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }

    @Override
    public Instant deserialize(JsonElement json, Type type, JsonDeserializationContext context)
            throws JsonParseException {
        return Instant.parse(json.getAsString());
    }
}