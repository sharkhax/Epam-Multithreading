package com.drobot.logistic_base.util;

import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator {

    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    public static int generateId() {
        return COUNTER.getAndIncrement();
    }
}
