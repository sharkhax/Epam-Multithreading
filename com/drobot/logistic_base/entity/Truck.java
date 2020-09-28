package com.drobot.logistic_base.entity;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Truck extends Entity implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(Truck.class);
    private Optional<Cargo> cargo;
    private boolean isLoaded;
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    public Truck(int id, Cargo cargo) {
        super(id);
        this.cargo = Optional.ofNullable(cargo);
        this.isLoaded = this.cargo.isPresent();
    }

    public Truck(int id) {
        super(id);
        this.cargo = Optional.empty();
        this.isLoaded = false;
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

    public void permit() {
        try {
            lock.lock();
            condition.signal();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void run() {
        LogisticBase logisticBase = LogisticBase.getInstance();
        logisticBase.offer(this);
        try {
            lock.lock();
            condition.await();
            logisticBase.service(this);
        } catch (InterruptedException e) {
            LOGGER.log(Level.ERROR, "Truck " + getId() + " is interrupted", e);
        } finally {
            lock.unlock();
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
        if (!super.equals(o)) {
            return false;
        }
        Truck truck = (Truck) o;
        if (isLoaded != truck.isLoaded) {
            return false;
        }
        if (!cargo.equals(truck.cargo)) {
            return false;
        }
        if (!lock.equals(truck.lock)) {
            return false;
        }
        return condition.equals(truck.condition);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + cargo.hashCode();
        result = 31 * result + (isLoaded ? 1 : 0);
        result = 31 * result + lock.hashCode();
        result = 31 * result + condition.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Truck{");
        sb.append("id=").append(getId());
        sb.append(", cargo=").append(cargo);
        sb.append(", isLoaded=").append(isLoaded);
        sb.append('}');
        return sb.toString();
    }
}
