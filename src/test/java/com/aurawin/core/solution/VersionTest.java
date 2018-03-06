package com.aurawin.core.solution;

import com.aurawin.core.solution.Version;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class VersionTest {

    @Test
    public void testVersion() throws IOException{
        Version v = new Version();
        v.loadFromResouce();
        assert(v.Build>0);

    }



}