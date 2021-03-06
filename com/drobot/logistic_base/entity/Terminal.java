package com.drobot.logistic_base.entity;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Terminal extends Entity {

    private static final Logger LOGGER = LogManager.getLogger(Terminal.class);
    private Optional<Truck> optionalTruck = Optional.empty();
    private final int SERVICE_TIME_SECONDS = new Random().nextInt(1) + 1;

    public Terminal(int id) {
        super(id);
    }

    public Optional<Truck> getTruck() {
        return optionalTruck;
    }

    boolean removeTruckIfPresent() {
        boolean result = false;
        if (optionalTruck.isPresent()) {
            optionalTruck = Optional.empty();
            result = true;
            LOGGER.log(Level.DEBUG, "Truck has been removed");
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
                Cargo cargo = truck.unload().orElseThrow();
                try {
                    TimeUnit.SECONDS.sleep(SERVICE_TIME_SECONDS);
                } catch (InterruptedException e) {
                    LOGGER.log(Level.WARN, Thread.currentThread().getName() + " is interrupted", e);
                }
                result = warehouse.collect(cargo);
            } else {
                Optional<Cargo> optionalCargo = warehouse.dispense();
                if (optionalCargo.isPresent()) {
                    Cargo cargo = optionalCargo.get();
                    truck.load(cargo);
                    try {
                        TimeUnit.SECONDS.sleep(SERVICE_TIME_SECONDS);
                    } catch (InterruptedException e) {
                        LOGGER.log(Level.WARN, Thread.currentThread().getName() + " is interrupted", e);
                    }
                    result = true;
                }
            }
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
