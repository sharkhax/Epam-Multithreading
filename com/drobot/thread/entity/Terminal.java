package com.drobot.thread.entity;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Terminal implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(Terminal.class);
    private Optional<Truck> optionalTruck = Optional.empty();
    private final int SERVICE_TIME_SECONDS = new Random().nextInt(5) + 3;

    public Optional<Truck> getTruck() {
        return optionalTruck;
    }

    public boolean removeTruckIfPresent() {
        boolean result = false;
        if (optionalTruck.isPresent()) {
            optionalTruck = Optional.empty();
            result = true;
            LOGGER.log(Level.INFO, "Truck has been removed");
        } else {
            LOGGER.log(Level.DEBUG, "No truck to remove");
        }
        return result;
    }

    public void setTruck(Truck truck) {
        this.optionalTruck = Optional.ofNullable(truck);
    }

    public boolean service() {
        boolean result = false;
        if (optionalTruck.isPresent()) {
            try {
                Truck truck = optionalTruck.get();
                Warehouse warehouse = Warehouse.getInstance();
                if (truck.isLoaded()) {
                    Optional<Cargo> optional = truck.unload();
                    LOGGER.log(Level.DEBUG, "Unloading truck...");
                    result = warehouse.collect(optional.orElseThrow());
                } else {
                    Optional<Cargo> optional = warehouse.dispense();
                    if (optional.isPresent()) {
                        LOGGER.log(Level.DEBUG, "Loading truck...");
                        result = truck.load(optional.get());
                    }
                }
                TimeUnit.SECONDS.sleep(SERVICE_TIME_SECONDS);
                LOGGER.log(Level.INFO, "Terminal has serviced the truck");
            } catch (InterruptedException e) {
                LOGGER.log(Level.ERROR, "Terminal thread has been interrupted.", e);
            }
        } else {
            LOGGER.log(Level.DEBUG, "No truck to service");
        }
        return result;
    }

    @Override
    public void run() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Terminal terminal = (Terminal) o;
        if (SERVICE_TIME_SECONDS != terminal.SERVICE_TIME_SECONDS) {
            return false;
        }
        return optionalTruck.equals(terminal.optionalTruck);
    }

    @Override
    public int hashCode() {
        int result = optionalTruck.hashCode();
        result = 31 * result + SERVICE_TIME_SECONDS;
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Terminal{");
        sb.append("optionalTruck=").append(optionalTruck);
        sb.append(", SERVICE_TIME_SECONDS=").append(SERVICE_TIME_SECONDS);
        sb.append('}');
        return sb.toString();
    }
}
