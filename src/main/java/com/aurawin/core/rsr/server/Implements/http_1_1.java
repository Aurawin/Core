package com.aurawin.core.rsr.server.Implements;

import com.aurawin.core.rsr.def.rsrResult;
import com.aurawin.core.rsr.Item;
import com.aurawin.core.rsr.Items;
import com.aurawin.core.rsr.def.http.*;

public class http_1_1 extends Item {
    public volatile Request Request;
    public volatile Response Response;

    public http_1_1(Items aOwner) {
        super(aOwner);
        Request=new Request(this);
        Response=new Response(this);

    }

    @Override
    protected rsrResult onPeek() {
        return null;
    }

    @Override
    protected rsrResult onProcess() {
        return null;
    }

    @Override
    protected rsrResult onDisconnected() {
        return null;
    }

    @Override
    protected rsrResult onAccepted() {
        return null;
    }

    @Override
    protected rsrResult onRejected() {
        return null;
    }

    @Override
    protected rsrResult onError() {
        return null;
    }

    @Override
    protected rsrResult onFinalize() {
        Request.Release();
        Response.Release();

        Request=null;
        Response = null;

        return rsrResult.rSuccess;
    }

    @Override
    protected rsrResult onInitialize() {
        return null;

    }
}
