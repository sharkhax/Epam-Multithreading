import com.drobot.thread.entity.Cargo;
import com.drobot.thread.entity.LogisticBase;
import com.drobot.thread.entity.Truck;
import com.drobot.thread.entity.Warehouse;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        LogisticBase logisticBase = LogisticBase.getInstance();
        logisticBase.start();
        for (int i = 0; i < 500; i++) {
            Cargo cargo = null;
            if (new Random().nextBoolean()) {
                cargo = new Cargo(String.valueOf(i), new Random().nextBoolean());
            }
            Truck truck = new Truck(String.valueOf(i), cargo);
            truck.start();
            TimeUnit.MILLISECONDS.sleep(50);
        }
        TimeUnit.SECONDS.sleep(60);
        logisticBase.interrupt();
        System.out.println(logisticBase);
        Warehouse warehouse = Warehouse.getInstance();
        System.out.println(warehouse.getLoadedCargo());
        System.out.println(warehouse.getUnloadedCargo());
    }
}
