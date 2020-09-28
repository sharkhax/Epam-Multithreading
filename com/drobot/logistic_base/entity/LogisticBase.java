package com.drobot.logistic_base.entity;

import com.drobot.logistic_base.comparator.PerishableCargoTruckComparator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;
import java.util.Deque;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LogisticBase extends Thread {

    private static class Singleton {
        private static final LogisticBase INSTANCE = new LogisticBase();
    }

    private static final Logger LOGGER = LogManager.getLogger(LogisticBase.class);
    private static final int TERMINALS_NUMBER = 8;
    private static final int COOL_DOWN_AFTER_SERVICING_ATTEMPT_SECS = 10;
    private static final int MAX_ATTEMPTS_NUMBER = 6;
    private final Queue<Truck> truckQueue;
    private final Deque<Terminal> freeTerminals;
    private final Deque<Terminal> givenTerminals;
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private final Semaphore terminalSemaphore;
    private final Semaphore queueSemaphore;

    private LogisticBase() {
        String threadName = "Logistic base";
        setName(threadName);
        Comparator<Truck> comparator = new PerishableCargoTruckComparator();
        truckQueue = new PriorityQueue<>(comparator);
        freeTerminals = new ConcurrentLinkedDeque<>();
        givenTerminals = new ConcurrentLinkedDeque<>();
        terminalSemaphore = new Semaphore(TERMINALS_NUMBER, true);
        queueSemaphore = new Semaphore(0, true);
        for (int i = 0; i < TERMINALS_NUMBER; i++) {
            Terminal terminal = new Terminal(i);
            freeTerminals.push(terminal);
        }
        LOGGER.log(Level.INFO, "Logistic base has been created");
    }

    public static LogisticBase getInstance() {
        return Singleton.INSTANCE;
    }

    public boolean offer(Truck truck) {
        boolean result = false;
        try {
            lock.lock();
            if (truck != null) {
                result = truckQueue.offer(truck);
                queueSemaphore.release();
            } else {
                LOGGER.log(Level.ERROR, "Truck is null, can't offer");
            }
        } finally {
            lock.unlock();
        }
        return result;
    }

    public boolean service(Truck truck) {
        boolean result = false;
        Terminal terminal = null;
        try {
            terminalSemaphore.acquire();
            if (truck != null) {
                terminal = freeTerminals.poll();
                if (terminal != null) {
                    givenTerminals.offer(terminal);
                    terminal.setTruck(truck);
                    boolean isServiced = false;
                    int attemptCounter = 0;
                    while (!isServiced && attemptCounter < MAX_ATTEMPTS_NUMBER) {
                        if (terminal.service()) {
                            isServiced = true;
                            result = true;
                            freeTerminal(terminal);
                            LOGGER.log(Level.INFO, "Truck " + truck.getId() + " has been serviced");
                        } else {
                            LOGGER.log(Level.WARN, "Truck " + truck.getId() + " wasn't serviced, retrying");
                            TimeUnit.SECONDS.sleep(COOL_DOWN_AFTER_SERVICING_ATTEMPT_SECS);
                            attemptCounter++;
                        }
                    }
                    if (!isServiced) {
                        LOGGER.log(Level.WARN, "Attempts number exceeded, truck "
                                + truck.getId() + " hasn't been serviced");
                        freeTerminal(terminal);
                    }
                } else {
                    LOGGER.log(Level.ERROR, "No free terminals, but semaphore permits truck to be serviced");
                }
            } else {
                LOGGER.log(Level.ERROR, "No truck in queue");
            }
        } catch (InterruptedException e) {
            if (terminal != null) {
                freeTerminal(terminal);
                Thread.currentThread().interrupt();
            }
        } finally {
            terminalSemaphore.release();
        }
        return result;
    }

    public int getGivenTerminalsNumber() {
        return givenTerminals.size();
    }

    public int getTruckQueueSize() {
        return truckQueue.size();
    }

    @Override
    public void run() {
        while (!interrupted()) {
            while (truckQueue.isEmpty()) {
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                    queueSemaphore.acquire();
                } catch (InterruptedException e) {
                    return;
                }
            }
            try {
                lock.lock();
                Truck truck = truckQueue.poll();
                if (truck != null) {
                    truck.permit();
                } else {
                    LOGGER.log(Level.ERROR, "Truck is null somehow");
                }
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LogisticBase{");
        sb.append("truckQueue=").append(truckQueue);
        sb.append(", freeTerminals=").append(freeTerminals);
        sb.append(", givenTerminals=").append(givenTerminals);
        sb.append(", lock=").append(lock);
        sb.append(", condition=").append(condition);
        sb.append(", semaphore=").append(terminalSemaphore);
        sb.append('}');
        return sb.toString();
    }

    private boolean freeTerminal(Terminal terminal) {
        boolean result = false;
        if (terminal != null) {
            if (givenTerminals.remove(terminal)) {
                terminal.removeTruckIfPresent();
                freeTerminals.offer(terminal);
                result = true;
            }
        }
        return result;
    }
}
