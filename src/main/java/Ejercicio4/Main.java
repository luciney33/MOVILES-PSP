package Ejercicio4;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class Main {
        public static void main(String[] args) throws InterruptedException {
            final Logger log = Logger.getLogger(Main.class.getName());
            int numCoches = 200;
            Parking parking = new Parking(20, 5, 10);

            ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

            for (int i = 1; i <= numCoches; i++) {
                TipoVehiculo tipo = (Math.random() < 0.2) ? TipoVehiculo.VIP : TipoVehiculo.NORMAL;
                executor.submit(new Coche(i, tipo, parking));
            }

            executor.shutdown();
            while (!executor.isTerminated()) {
                Thread.sleep(500);
            }

            log.info("Ingresos totales: " + parking.getIngresos() + "€");
        }
}
