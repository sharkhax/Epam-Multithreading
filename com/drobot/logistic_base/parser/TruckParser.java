package com.drobot.logistic_base.parser;

import com.drobot.logistic_base.entity.Cargo;
import com.drobot.logistic_base.entity.Truck;
import com.drobot.logistic_base.util.IdGenerator;

public class TruckParser {

    private static final String NULL = "null";

    public Truck parseTruck(String string) {
        Truck result;
        int truckId = IdGenerator.generateId();
        if (!string.equals(NULL)) {
            boolean isCargoPerishable = Boolean.parseBoolean(string);
            Cargo cargo = new Cargo(isCargoPerishable);
            result = new Truck(truckId, cargo);
        } else {
            result = new Truck(truckId);
        }
        return result;
    }
}
