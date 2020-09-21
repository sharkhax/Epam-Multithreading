package com.drobot.thread.entity;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Deque;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LogisticBase extends Thread {

    private static class Singleton {
        private static final LogisticBase INSTANCE = new LogisticBase();
    }

    private static final Logger LOGGER = LogManager.getLogger(LogisticBase.class);
    private static final int TERMINALS_NUMBER = 8;
    private final Deque<Truck> primaryQueue;
    private final Deque<Truck> secondaryQueue;
    private final Deque<Terminal> freeTerminals;
    private final Deque<Terminal> givenTerminals;
    private final Lock lock = new ReentrantLock();
    private final AtomicInteger truckCounter = new AtomicInteger(0);
    private final Semaphore semaphore;

    private LogisticBase() {
        primaryQueue = new ConcurrentLinkedDeque<>();
        secondaryQueue = new ConcurrentLinkedDeque<>();
        freeTerminals = new ConcurrentLinkedDeque<>();
        givenTerminals = new ConcurrentLinkedDeque<>();
        for (int i = 0; i < TERMINALS_NUMBER; i++) {
            Terminal terminal = new Terminal();
            freeTerminals.offer(terminal);
        }
        semaphore = new Semaphore(TERMINALS_NUMBER, true);
        LOGGER.log(Level.INFO, "Logistic base has been created");
    }

    public static LogisticBase getInstance() {
        return Singleton.INSTANCE;
    }

    public boolean offer(Truck truck) {
        try {
            lock.lock();
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
        } finally {
            lock.unlock();
        }
        return true;
    }

    public boolean service(Truck truck) {
        boolean result = false;
        try {
            semaphore.acquire();
            Terminal terminal = freeTerminals.poll();
            givenTerminals.offer(terminal);
            if (terminal != null) {
                terminal.setTruck(truck);
                if (terminal.service()) {
                    terminal.removeTruckIfPresent();
                    givenTerminals.remove(terminal);
                    freeTerminals.offer(terminal);
                    truckCounter.incrementAndGet();
                    result = true;
                    LOGGER.log(Level.INFO, "Truck " + truck.getTruckId() + " has been serviced");
                }
            } else {
                LOGGER.log(Level.ERROR, "Terminal is null somehow");
            }
        } catch (InterruptedException ignored) {
        } finally {
            semaphore.release();
        }
        return result;
    }

//    public boolean service() {
//        boolean result = false;
//        Terminal terminal;
//        try {
//            semaphore.acquire();
//        } catch (InterruptedException ignored) {
//        }
//        try {
//            lock.lock();
//            terminal = freeTerminals.peek();
//            if (terminal != null) {
//                Optional<Truck> optionalTruck = getTruckFromQueue();
//                if (optionalTruck.isPresent()) {
//                    Truck truck = optionalTruck.get();
//                    freeTerminals.pollFirst();
//                    givenTerminals.offer(terminal);
//                    terminal.setTruck(truck);
//                } else {
//                    LOGGER.log(Level.DEBUG, "The secondary queue is empty");
//                    LOGGER.log(Level.INFO, "Both queues are empty");
//                }
//            } else {
//                LOGGER.log(Level.DEBUG, "No terminals available");
//            }
//        } finally {
//            lock.unlock();
//        }
//        if (terminal != null && terminal.service()) {
//            terminal.removeTruckIfPresent();
//            givenTerminals.remove(terminal);
//            freeTerminals.offer(terminal);
//            result = true;
//            truckCounter.incrementAndGet();
//            LOGGER.log(Level.INFO, "Servicing complete");
//        }
//        semaphore.release();
//        return result;
//    }

    private boolean admit() {
        boolean result = false;
        Truck truck = primaryQueue.pollFirst();
        if (truck == null) {
            truck = secondaryQueue.pollFirst();
        }
        if (truck != null) {
            truck.setPermission(true);
        }
        return result;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            admit();
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                LOGGER.log(Level.INFO, "Interrupted", e);
                interrupt();
            }
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LogisticBase{");
        sb.append("primaryQueue=").append(primaryQueue);
        sb.append(", secondaryQueue=").append(secondaryQueue);
        sb.append(", freeTerminals=").append(freeTerminals);
        sb.append(", givenTerminals=").append(givenTerminals);
        sb.append(", truckCounter=").append(truckCounter);
        sb.append('}');
        return sb.toString();
    }
}
