package com.drobot.thread.entity;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Terminal {

    private static final Logger LOGGER = LogManager.getLogger(Terminal.class);
    private Optional<Truck> optionalTruck = Optional.empty();
    private final int SERVICE_TIME_SECONDS = new Random().nextInt(1) + 1;

    public Optional<Truck> getTruck() {
        return optionalTruck;
    }

    boolean removeTruckIfPresent() {
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

    void setTruck(Truck truck) {
        this.optionalTruck = Optional.ofNullable(truck);
    }

    boolean service() {
        boolean result = false;
        if (optionalTruck.isPresent()) {
            Truck truck = optionalTruck.get();
            Warehouse warehouse = Warehouse.getInstance();
            if (truck.isLoaded()) {
                Optional<Cargo> optionalCargo = truck.unload();
                LOGGER.log(Level.DEBUG, "Unloading truck...");
                result = warehouse.collect(optionalCargo.orElseThrow());
            } else {
                Optional<Cargo> optionalCargo = warehouse.dispense();
                if (optionalCargo.isPresent()) {
                    LOGGER.log(Level.DEBUG, "Loading truck...");
                    result = truck.load(optionalCargo.get());
                }
            }
            try {
                TimeUnit.SECONDS.sleep(SERVICE_TIME_SECONDS);
                LOGGER.log(Level.INFO, "Terminal has serviced the truck " + truck.getTruckId());
            } catch (InterruptedException e) {
                LOGGER.log(Level.INFO, "Terminal servicing has been interrupted", e);
                truck.interrupt();
            }
        } else {
            LOGGER.log(Level.DEBUG, "No truck to service");
        }
        return result;
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
