package com.aurawin.core.plugin;

import com.aurawin.core.stored.entities.Plugin;

public class Info extends Plugin{
    long ProviderId = 0;
    boolean Enabled  = true;
    boolean Anonymous   = false;
    boolean NotifyOnBuffersChanged = false;
    com.aurawin.core.plugin.annotations.Plugin Annotation;
}
