package com.drobot.logistic_base.entity;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Warehouse {

    private static class Singleton {
        private static final Warehouse INSTANCE = new Warehouse();
    }

    private static final Logger LOGGER = LogManager.getLogger(Warehouse.class);
    private final AtomicInteger cargoCollected;
    private final AtomicInteger cargoDispensed;

    private Warehouse() {
        cargoCollected = new AtomicInteger(0);
        cargoDispensed = new AtomicInteger(0);
        LOGGER.log(Level.INFO, "Warehouse has been created");
    }

    public static Warehouse getInstance() {
        return Singleton.INSTANCE;
    }

    public AtomicInteger getCargoCollected() {
        return cargoCollected;
    }

    public AtomicInteger getCargoDispensed() {
        return cargoDispensed;
    }

    Optional<Cargo> dispense() {
        Optional<Cargo> result;
        Cargo cargo = new Cargo(new Random().nextBoolean());
        result = Optional.of(cargo);
        cargoDispensed.incrementAndGet();
        LOGGER.log(Level.DEBUG, "Cargo has been dispensed");
        return result;
    }

    boolean collect(Cargo cargo) {
        boolean result = false;
        if (cargo != null) {
            cargoCollected.incrementAndGet();
            result = true;
            LOGGER.log(Level.DEBUG, "Cargo has been collected");
        }
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Warehouse{");
        sb.append("cargoCollected=").append(cargoCollected);
        sb.append(", cargoDispensed=").append(cargoDispensed);
        sb.append('}');
        return sb.toString();
    }
}
