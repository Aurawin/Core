package com.aurawin.core.rsr.server.Implements;

import com.aurawin.core.rsr.def.rsrResult;
import static com.aurawin.core.rsr.def.rsrResult.*;
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
    public http_1_1 newInstance(Items aOwner){
        return new http_1_1(aOwner);
    }
    @Override
    protected rsrResult onPeek() {
        return Request.Peek();
    }

    @Override
    protected rsrResult onProcess() {
        if (Request.Read()==rSuccess) {
            // todo process request
            Request.Resolve();
            return rSuccess;
        } else {
            return rFailure;
        }
    }

    @Override
    protected rsrResult onDisconnected() {
        return rSuccess;
    }

    @Override
    protected rsrResult onAccepted() {
        return rSuccess;
    }

    @Override
    protected rsrResult onRejected() {
        return rSuccess;
    }

    @Override
    protected rsrResult onError() {
        return rSuccess;
    }

    @Override
    protected rsrResult onFinalize() {
        Request.Release();
        Response.Release();

        Request=null;
        Response = null;

        return rSuccess;
    }

    @Override
    protected rsrResult onInitialize() {
        return rSuccess;

    }
}
