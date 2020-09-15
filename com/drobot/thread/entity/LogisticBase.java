package com.drobot.thread.entity;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;

public class LogisticBase implements Runnable {

    private static class Singleton {
        private static final LogisticBase INSTANCE = new LogisticBase();
    }

    private static final Logger LOGGER = LogManager.getLogger(LogisticBase.class);
    private static final int TERMINALS_NUMBER = 8;
    private final Deque<Truck> primaryQueue;
    private final Deque<Truck> secondaryQueue;
    private final Deque<Terminal> freeTerminals;
    private final Deque<Terminal> givenTerminals;

    private LogisticBase() {
        primaryQueue = new ConcurrentLinkedDeque<>();
        secondaryQueue = new ConcurrentLinkedDeque<>();
        freeTerminals = new ConcurrentLinkedDeque<>();
        givenTerminals = new ArrayDeque<>(TERMINALS_NUMBER);
        for (int i = 0; i < TERMINALS_NUMBER; i++) {
            Terminal terminal = new Terminal();
            freeTerminals.offer(terminal);
        }
        LOGGER.log(Level.INFO, "Logistic base has been created");
    }

    public static LogisticBase getInstance() {
        return Singleton.INSTANCE;
    }

    public boolean offer(Truck truck) {
        if (!truck.isLoaded()) {
            secondaryQueue.offer(truck);
        } else {
            Optional<Cargo> optional = truck.getCargo();
            Cargo cargo = optional.orElseThrow();
            if (cargo.isPerishable()) {
                primaryQueue.offer(truck);
            } else {
                secondaryQueue.offer(truck);
            }
        }
        return true;
    }

    public boolean service() {
        boolean result = false;
        Truck truck = primaryQueue.pollFirst();
        if (truck == null) {
            LOGGER.log(Level.DEBUG, "The primary queue is empty");
            truck = secondaryQueue.pollFirst();
        }
        if (truck != null) {
            Terminal terminal = freeTerminals.remove();
            if (terminal != null) {
                givenTerminals.offer(terminal);
                terminal.setTruck(truck);
                if (terminal.service()) {
                    terminal.removeTruckIfPresent();
                    givenTerminals.remove(terminal);
                    freeTerminals.offer(terminal);
                    result = true;
                    LOGGER.log(Level.INFO, "Servicing complete");
                }
            } else {
                LOGGER.log(Level.DEBUG, "No terminals available");
            }
        } else {
            LOGGER.log(Level.DEBUG, "The secondary queue is empty");
            LOGGER.log(Level.INFO, "Both queues are empty");
        }
        return result;
    }

    @Override
    public void run() {

    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LogisticBase{");
        sb.append("primaryQueue=").append(primaryQueue);
        sb.append(", secondaryQueue=").append(secondaryQueue);
        sb.append(", freeTerminals=").append(freeTerminals);
        sb.append(", givenTerminals=").append(givenTerminals);
        sb.append('}');
        return sb.toString();
    }
}
