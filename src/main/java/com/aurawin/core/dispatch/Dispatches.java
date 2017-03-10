package com.aurawin.core.dispatch;


import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.*;

public class Dispatches {

    public class Items {
        ExecutorService service = Executors.newCachedThreadPool();
        public void Queue(Dispatch d) {
            if (d instanceof Callable)
              service.submit(d);
        }

    }
}
