package com.aurawin.core.json;

import com.aurawin.core.json.Serializers.InstantConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.Instant;

public class Builder {
    private GsonBuilder _builder;
    public Builder() {
        _builder = new GsonBuilder();
        _builder.excludeFieldsWithoutExposeAnnotation();
        _builder.registerTypeAdapter(Instant.class, new InstantConverter());
    }
    public Gson Create(){
        return _builder.create();
    }
}
