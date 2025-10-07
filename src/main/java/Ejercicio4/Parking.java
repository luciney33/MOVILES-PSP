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
    private final BlockingQueue<Coche> colaEspera = new LinkedBlockingQueue<>(10);
    private final Lock entrada = new ReentrantLock();
    private final Lock salida = new ReentrantLock();
    private double ingresos = 0;
    private final Logger log = Logger.getLogger(Parking.class.getName());
    private int vehiculosProcesados = 0;
    private int vehiculosAtendidos = 0;
    private int vehiculosRechazados = 0;
    private long tiempoEstancia = 0;
    private int ocupacionMax = 0;
    private int cochesTiempo = 0;


    public Parking(int plazasNormales, int plazasVIP) {
        this.plazasNormales = new Semaphore(plazasNormales);
        this.plazasVIP = new Semaphore(plazasVIP);
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
                if (plazasNormales.tryAcquire()) {
                    siPuede = true;
                    log.info(LocalTime.now() + " Coche: " + coche.getId() + " entra en plaza NORMAL");
                }
            }

            if (!siPuede) {
                if (colaEspera.offer(coche)) {
                    log.info(LocalTime.now() + " Coche: " + coche.getId() + " esperando en cola");
                } else {
                    vehiculosRechazados++;
                    log.info(LocalTime.now() + " Coche: " + coche.getId() + " (" + coche.getTipoVehiculo() + ") se va, parking+cola llenos");
                    return false;
                }
            } else {
                vehiculosAtendidos++;
                int ocupacionActual = 25 - (plazasNormales.availablePermits() + plazasVIP.availablePermits());
                ocupacionMax = Math.max(ocupacionMax, ocupacionActual);
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
                if (plazasVIP.availablePermits() < 5)
                    plazasVIP.release();
                else
                    plazasNormales.release();
            } else {
                plazasNormales.release();
            }

            double pago = coche.getTipoVehiculo().getTarifaPorMinuto() * minutos;
            ingresos += pago;
            log.info(LocalTime.now() + " Coche: " + coche.getId() + " sale. Pagó: " + String.format("%.2f€", pago));

            Coche siguiente = colaEspera.poll();
            if (siguiente != null) {
                entrar(siguiente);
            }
        } finally {
            salida.unlock();
        }
    }

    public synchronized void cocheProcesado() {
        vehiculosProcesados++;
    }

    public synchronized void agregarTiempoEstancia(long duracionMs) {
        tiempoEstancia += duracionMs;
        cochesTiempo++;
    }

    public void estadisticas() {
        System.out.println("------- RESUMEN DEL DÍA ---");
        System.out.println("Vehículos procesados: " + vehiculosProcesados);
        System.out.println("Vehículos atendidos: " + vehiculosAtendidos +
                " (" + String.format("%.1f", (vehiculosAtendidos * 100.0 / vehiculosProcesados)) + "%)");
        System.out.println("Vehículos rechazados: " + vehiculosRechazados);

        double promedio = (cochesTiempo == 0) ? 0 : (tiempoEstancia / (cochesTiempo * 1000.0));
        System.out.println("Tiempo promedio de estancia: " + String.format("%.1f", promedio) + "s");

        System.out.println("Ingresos totales: " + String.format("%.2f€", ingresos));
        System.out.println("Ocupación máxima: " + ocupacionMax + "/25 plazas (" +
                String.format("%.1f", (ocupacionMax * 100.0 / 25)) + "%)");
    }
}
