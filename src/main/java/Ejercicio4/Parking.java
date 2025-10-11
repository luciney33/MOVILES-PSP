package Ejercicio4;

import lombok.Data;

import java.time.LocalTime;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


@Data
public class Parking {
    private final Semaphore plazasNormales;
    private final Semaphore plazasVIP;
    private final BlockingQueue<Coche> colaEspera = new LinkedBlockingQueue<>(10);
    private final Lock entrada = new ReentrantLock();
    private final Lock salida = new ReentrantLock();
    private double ingresos = 0;
    private AtomicInteger vehiculosProcesados = new AtomicInteger();
    private AtomicInteger vehiculosAtendidos = new AtomicInteger();
    private AtomicInteger vehiculosRechazados = new AtomicInteger();
    private long tiempoEstancia = 0;
    private int ocupacionMax = 0;
    private AtomicInteger cochesTiempo = new AtomicInteger();


    public Parking(int plazasNormales, int plazasVIP) {
        this.plazasNormales = new Semaphore(plazasNormales);
        this.plazasVIP = new Semaphore(plazasVIP);
    }

    public void iniciarServicio() throws InterruptedException {
        int numCoches = 200;
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

        for (int i = 1; i <= numCoches; i++) {
            TipoVehiculo tipo;
            if (Math.random() < 0.2) {
                tipo = TipoVehiculo.VIP;
            } else {
                tipo = TipoVehiculo.NORMAL;
            }
            executor.submit(new Coche(i, tipo, this));
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
            Thread.sleep(500);
        }
        estadisticas();

    }

    public void cocheProcesado() {
        vehiculosProcesados.incrementAndGet();
    }

    public void agregarTiempoEstancia(long duracionMs) {
        tiempoEstancia += duracionMs;
        cochesTiempo.incrementAndGet();
    }

    public boolean entrar(Coche coche) throws InterruptedException {
        entrada.lock();
        try {
            boolean siPuede = false;
            if (coche.getTipoVehiculo() == TipoVehiculo.VIP) {
                if (plazasVIP.tryAcquire()) {
                    siPuede = true;
                    System.out.println(LocalTime.now()+" Coche: " + coche.getId() + " ENTRAAA en plaza VIP");
                } else if (plazasNormales.tryAcquire()) {
                    siPuede = true;
                    System.out.println(LocalTime.now()+" Coche: " + coche.getId() + " ENTRAAA en plaza NORMAL");
                }
            } else {
                if (plazasNormales.tryAcquire()) {
                    siPuede = true;
                    System.out.println(LocalTime.now()+" Coche: " + coche.getId() + " ENTRAAA en plaza NORMAL");
                }
            }

            if (!siPuede) {
                if (colaEspera.offer(coche)) {
                    System.out.println(LocalTime.now() + " Coche: " + coche.getId() + " ESPERAA en la cola");
                } else {
                    vehiculosRechazados.incrementAndGet();
                    System.out.println(LocalTime.now() + " Coche: " + coche.getId() + " (" + coche.getTipoVehiculo() + ") SE VAA, (parking+cola) llenos");
                }
            } else {
                vehiculosAtendidos.incrementAndGet();
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
            ingresos = ingresos+ pago;
            System.out.println(LocalTime.now() + " Coche: " + coche.getId() + " SALE. Pagó: " + String.format("%.2f€", pago));

            Coche siguiente = colaEspera.poll();
            if (siguiente != null) {
                entrar(siguiente);
            }
        } finally {
            salida.unlock();
        }
    }



    public void estadisticas() {
        System.out.println("----RESUMEN DEL DÍA ----------");
        System.out.println("Vehículos procesados: " + vehiculosProcesados);
        System.out.println("Vehículos atendidos: " + vehiculosAtendidos +
                " (" + String.format("%.1f", (vehiculosAtendidos.get() * 100.0 / vehiculosProcesados.get())) + "%)");
        System.out.println("Vehículos rechazados: " + vehiculosRechazados);

        double promedio;
        if (cochesTiempo.get() == 0) {
            promedio = 0;
        } else {
            double tiempoTotalSegundos = tiempoEstancia / 1000.0;
            promedio = tiempoTotalSegundos / cochesTiempo.get();
        }
        System.out.println("Tiempo promedio de estancia: " + String.format("%.1f", promedio) + "s");

        System.out.println("Ingresos totales: " + String.format("%.2f€", ingresos));
        System.out.println("Ocupación máxima: " + ocupacionMax + "/25 plazas (" +
                String.format("%.1f", (ocupacionMax * 100.0 / 25)) + "%)");
    }
}
