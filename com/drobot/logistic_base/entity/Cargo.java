package com.drobot.logistic_base.entity;

public class Cargo extends Entity {

    private boolean isPerishable;

    public Cargo(int id, boolean isPerishable) {
        super(id);
        this.isPerishable = isPerishable;
    }

    public Cargo(boolean isPerishable) {
        this.isPerishable = isPerishable;
    }

    public boolean isPerishable() {
        return isPerishable;
    }

    public void setPerishable(boolean perishable) {
        isPerishable = perishable;
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
        Cargo cargo = (Cargo) o;
        return isPerishable == cargo.isPerishable;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
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
