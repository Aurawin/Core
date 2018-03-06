package com.aurawin.core.json;

import com.aurawin.core.json.Builder;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import org.junit.Test;

import java.time.Instant;

import static org.junit.Assert.*;

public class BuilderTest {

    public class InstantObject{
        public InstantObject() {
            Now = Instant.now();
            Then = Instant.now();
        }
        @Expose(serialize = true, deserialize = true)
        public Instant Now;
        @Expose(serialize = true, deserialize = true)
        public Instant Then;
    }

    @Test
    public void testBuilder() {
        Builder bldr = new Builder();
        Gson gson = bldr.Create();
        InstantObject io = new InstantObject();
        String sIO=gson.toJson(io);

        InstantObject test = gson.fromJson(sIO,InstantObject.class);


    }
}