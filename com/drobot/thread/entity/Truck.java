package com.drobot.thread.entity;

import java.util.Optional;

public class Truck {

    private final String truckId;
    private Optional<Cargo> cargo;
    private boolean isLoaded;

    public Truck(String truckId) {
        this.truckId = truckId;
        this.cargo = Optional.empty();
        this.isLoaded = false;
    }

    public Truck(String truckId, Cargo cargo) {
        this.truckId = truckId;
        this.cargo = Optional.ofNullable(cargo);
        this.isLoaded = true;
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
