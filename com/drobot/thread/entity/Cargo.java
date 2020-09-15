package com.drobot.thread.entity;

public class Cargo {

    private final String cargoId;
    private final boolean isPerishable;

    public Cargo(String cargoId, boolean isPerishable) {
        this.cargoId = cargoId;
        this.isPerishable = isPerishable;
    }

    public boolean isPerishable() {
        return isPerishable;
    }

    public String getCargoId() {
        return cargoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Cargo cargo = (Cargo) o;
        if (!cargoId.equals(cargo.cargoId)) {
            return false;
        }
        return isPerishable == cargo.isPerishable;
    }

    @Override
    public int hashCode() {
        int result = cargoId != null ? cargoId.hashCode() : 0;
        result = 31 * result + (isPerishable ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Cargo{");
        sb.append("isPerishable=").append(isPerishable);
        sb.append('}');
        return sb.toString();
    }


}
