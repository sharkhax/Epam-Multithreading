package com.drobot.thread.entity;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class Truck extends Thread {

    private static final Logger LOGGER = LogManager.getLogger(Truck.class);
    private final String truckId;
    private Optional<Cargo> cargo;
    private boolean isLoaded;
    private final String threadName;
    private static final int TIMEOUT_BETWEEN_OFFERS_SECS = 5;
    private boolean permission = false;

    public Truck(String truckId) {
        this.truckId = truckId;
        this.cargo = Optional.empty();
        this.isLoaded = false;
        threadName = "Truck " + truckId;
    }

    public Truck(String truckId, Cargo cargo) {
        this.truckId = truckId;
        this.cargo = Optional.ofNullable(cargo);
        this.isLoaded = this.cargo.isPresent();
        threadName = "Truck " + truckId;
    }

    public String getTruckId() {
        return truckId;
    }

    public Optional<Cargo> getCargo() {
        return cargo;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public boolean load(Cargo cargo) {
        boolean result = false;
        if (this.cargo.isEmpty() && cargo != null) {
            this.cargo = Optional.of(cargo);
            isLoaded = true;
            result = true;
        }
        return result;
    }

    public Optional<Cargo> unload() {
        Optional<Cargo> result = cargo;
        if (result.isPresent()) {
            isLoaded = false;
            cargo = Optional.empty();
        }
        return result;
    }

    public boolean isPermission() {
        return permission;
    }

    public void setPermission(boolean permission) {
        this.permission = permission;
    }

    @Override
    public void run() {
        setName(threadName);
        LogisticBase logisticBase = LogisticBase.getInstance();
        boolean isOffered = false;
        while(!isOffered && !isInterrupted()) {
            isOffered = logisticBase.offer(this);
            if (!isOffered) {
                try {
                    TimeUnit.SECONDS.sleep(TIMEOUT_BETWEEN_OFFERS_SECS);
                } catch (InterruptedException e) {
                    LOGGER.log(Level.INFO, "Interrupted", e);
                    interrupt();
                }
            }
        }
        while (!permission) { // todo observer
            try {
                TimeUnit.SECONDS.sleep(TIMEOUT_BETWEEN_OFFERS_SECS);
            } catch (InterruptedException e) {
                LOGGER.log(Level.INFO, "Interrupted", e);
                interrupt();
            }
        }
        if (!logisticBase.service(this)) {
            LOGGER.log(Level.WARN, "Truck " + truckId + " hasn't been serviced");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Truck truck = (Truck) o;
        if (isLoaded != truck.isLoaded) {
            return false;
        }
        if (!truckId.equals(truck.truckId)) {
            return false;
        }
        return cargo.equals(truck.cargo);
    }

    @Override
    public int hashCode() {
        int result = truckId.hashCode();
        result = 31 * result + cargo.hashCode();
        result = 31 * result + (isLoaded ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Truck{");
        sb.append("truckId='").append(truckId).append('\'');
        sb.append(", cargo=").append(cargo);
        sb.append(", isLoaded=").append(isLoaded);
        sb.append('}');
        return sb.toString();
    }
}
