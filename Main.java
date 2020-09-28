import com.drobot.logistic_base.entity.Cargo;
import com.drobot.logistic_base.entity.LogisticBase;
import com.drobot.logistic_base.entity.Truck;
import com.drobot.logistic_base.entity.Warehouse;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        final int trucksNumber = 10000;
        LogisticBase logisticBase = LogisticBase.getInstance();
        logisticBase.start();
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException ignore) {
        }
        for (int i = 0; i < trucksNumber; i++) {
            Cargo cargo = null;
            if (new Random().nextBoolean()) {
                cargo = new Cargo(new Random().nextBoolean());
            }
            Truck truck = new Truck(i, cargo);
            Thread thread = new Thread(truck);
            thread.setName("Truck " + i);
            thread.start();
        }
        try {
            TimeUnit.SECONDS.sleep(660);
        } catch (InterruptedException ignore) {
        }
        logisticBase.interrupt();
        LOGGER.log(Level.INFO, "Given terminals left: " + logisticBase.getGivenTerminalsNumber()
                + ", truck queue size: " + logisticBase.getTruckQueueSize());
        Warehouse warehouse = Warehouse.getInstance();
        int collected = warehouse.getCargoCollected().intValue();
        int dispensed = warehouse.getCargoDispensed().intValue();
        LOGGER.log(Level.INFO, "Cargo collected: " + collected
                + ", cargo dispensed: " + dispensed + ", total: " + (collected + dispensed));
    }
}
