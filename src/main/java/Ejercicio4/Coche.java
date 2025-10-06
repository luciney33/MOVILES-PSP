package Ejercicio4;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

@Data
@AllArgsConstructor
public class Coche implements Runnable{
    private int id;
    private TipoVehiculo tipoVehiculo;
    private Parking parking;
    private final Logger log = Logger.getLogger(Coche.class.getName());

    public void run() {
        try {
            if (!parking.entrar(this)) {
                log.info(LocalTime.now() + " Coche: " + id + " (" + tipoVehiculo + ") se va, parking lleno");
                return;
            }

            int tiempoEstancia = ThreadLocalRandom.current().nextInt(10, 31);
            Thread.sleep(tiempoEstancia * 1000L);

            parking.salir(this, tiempoEstancia);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
