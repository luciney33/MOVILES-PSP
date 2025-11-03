package Ejercicio4;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;
import java.util.concurrent.ThreadLocalRandom;

@Data
@AllArgsConstructor
public class Coche implements Runnable{
    private int id;
    private TipoVehiculo tipoVehiculo;
    private Parking parking;

//NO CONTROLA LOS COCHES QUE SE QUEDAN ESPERANDO FUERA DEL PARKING
    public void run() {
        try {
            parking.cocheProcesado();
            long tiempoEntrada = System.currentTimeMillis();

            Thread.sleep(2000);

            if (!parking.entrar(this)) {
                System.out.println(LocalTime.now() + " Coche: " + id + " (" + tipoVehiculo + ") SE VA, parking lleno");
            }

            int tiempoEstancia = ThreadLocalRandom.current().nextInt(10, 31);
            Thread.sleep(tiempoEstancia * 1000L);

            long duracion = System.currentTimeMillis() - tiempoEntrada;
            parking.agregarTiempoEstancia(duracion);

            Thread.sleep(1000);

            parking.salir(this, tiempoEstancia);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
