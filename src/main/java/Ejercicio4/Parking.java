package Ejercicio4;

import lombok.Data;

import java.time.LocalTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

@Data
public class Parking {
    private final Semaphore plazasNormales;
    private final Semaphore plazasVIP;
    private final BlockingQueue<Coche> colaEspera;
    private final Lock entrada = new ReentrantLock();
    private final Lock salida = new ReentrantLock();
    private double ingresos = 0;
    private final Logger log = Logger.getLogger(Parking.class.getName());


    public Parking(int plazasNormales, int plazasVIP, int colaMax) {
        this.plazasNormales = new Semaphore(plazasNormales);
        this.plazasVIP = new Semaphore(plazasVIP);
        this.colaEspera = new LinkedBlockingQueue<>(colaMax);
    }

    public boolean entrar(Coche coche) throws InterruptedException {
        entrada.lock();
        try {
            boolean siPuede = false;

            if (coche.getTipoVehiculo() == TipoVehiculo.VIP) {
                if (plazasVIP.tryAcquire()) {
                    siPuede = true;
                    log.info(LocalTime.now() + " Coche: " + coche.getId() + " entra en plaza VIP");
                } else if (plazasNormales.tryAcquire()) {
                    siPuede = true;
                    log.info(LocalTime.now() + " Coche: " + coche.getId() + " entra en plaza NORMAL");
                }
            } else {
                siPuede = plazasNormales.tryAcquire();
                if (siPuede) {
                    log.info(LocalTime.now() + " Coche: " + coche.getId() + " entra en plaza NORMAL");
                }
            }

            if (!siPuede) {
                siPuede = colaEspera.offer(coche);
                if (siPuede) {
                    log.info(LocalTime.now() + " Coche: " + coche.getId() + " esperando en cola");
                }
            }
            return siPuede;
        } finally {
            entrada.unlock();
        }

    }

    public void salir(Coche coche, int minutos) throws InterruptedException {
        salida.lock();
        try {
            if (coche.getTipoVehiculo() == TipoVehiculo.VIP) {
                if (plazasVIP.availablePermits() < 5) plazasVIP.release();
                else plazasNormales.release();
            } else {
                plazasNormales.release();
            }

            ingresos += coche.getTipoVehiculo().getTarifaPorMinuto() * minutos;

            log.info(LocalTime.now() + " Coche: " + coche.getId() + " sale, pagó: " +
                    (coche.getTipoVehiculo().getTarifaPorMinuto() * minutos) + "€");

            Coche siguiente = colaEspera.poll();
            if (siguiente != null) {
                entrar(siguiente);
            }
        } finally {
            salida.unlock();
        }
    }
}
