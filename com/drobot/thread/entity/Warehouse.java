package com.drobot.thread.entity;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Deque;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class Warehouse {

    private static class Singleton {
        private static final Warehouse INSTANCE = new Warehouse();
    }

    private static final Logger LOGGER = LogManager.getLogger(Warehouse.class);
    private final Deque<Cargo> storage;
    private static final int DEFAULT_CARGO_AMOUNT = 50;
    private AtomicInteger unloadedCargo = new AtomicInteger(0);
    private AtomicInteger loadedCargo = new AtomicInteger(0);

    private Warehouse() {
        storage = new ConcurrentLinkedDeque<>();
        for (int i = 0; i < DEFAULT_CARGO_AMOUNT; i++) {
            Cargo cargo = new Cargo(UUID.randomUUID().toString(), new Random().nextBoolean());
            storage.offer(cargo);
        }
        LOGGER.log(Level.INFO, "Warehouse has been created and filled");
    }

    public static Warehouse getInstance() {
        return Singleton.INSTANCE;
    }

    public AtomicInteger getUnloadedCargo() {
        return unloadedCargo;
    }

    public AtomicInteger getLoadedCargo() {
        return loadedCargo;
    }

    Optional<Cargo> dispense() {
        Cargo cargo = storage.poll();
        Optional<Cargo> result;
        if (cargo != null) {
            LOGGER.log(Level.DEBUG, "Cargo has been dispensed");
            result = Optional.of(cargo);
            unloadedCargo.incrementAndGet();
        } else {
            LOGGER.log(Level.WARN, "The warehouse is empty");
            result = Optional.empty();
        }
        return result;
    }

    boolean collect(Cargo cargo) {
        boolean result = storage.offer(cargo);
        if (result) {
            LOGGER.log(Level.DEBUG, "Warehouse collected the cargo");
            loadedCargo.incrementAndGet();
        } else {
            LOGGER.log(Level.WARN, "Warehouse did not collect the cargo");
        }
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Warehouse{");
        sb.append("storage=").append(storage);
        sb.append(", unloadedCargo=").append(unloadedCargo);
        sb.append(", loadedCargo=").append(loadedCargo);
        sb.append('}');
        return sb.toString();
    }
}
