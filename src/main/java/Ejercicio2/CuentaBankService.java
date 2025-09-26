package Ejercicio2;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CuentaBankService {
    private CuentaBankInterface cb;
    public CuentaBankService(CuentaBankInterface cb) {
        this.cb = cb;
    }

    public void work(String version) {
        Thread[] hilos = new Thread[50];
        AtomicInteger ingresos = new AtomicInteger(0);
        AtomicInteger exitosas = new AtomicInteger(0);
        AtomicInteger fallidas = new AtomicInteger(0);
        for (int i = 0; i < hilos.length; i++) {
            Thread hilo = Thread.startVirtualThread(() -> {
                try {
                    for (int j = 0; j < 10; j++) {
                        double prob = Math.random();
                        if (prob <= 0.6) {
                            double cantidadRetiro = (Math.random() * 99)+1;
                            boolean exito = cb.retirar(cantidadRetiro);
                            if (exito) exitosas.incrementAndGet();
                            else fallidas.incrementAndGet();
                        } else {
                            double cantidadIngreso = (Math.random() * 49)+1;
                            cb.ingresar(cantidadIngreso);
                            ingresos.incrementAndGet();
                        }
                        Thread.sleep((int) (Math.random() * 201) + 100);
                    }

                } catch (InterruptedException e) {
                    System.out.println("El hilo ha sido interrumpido");
                }
            });
            hilos[i] = hilo;
        }
        for (int i = 0; i < hilos.length; i++) {
            try {
                hilos[i].join();
            } catch (InterruptedException e) {
                System.out.println("El hilo ha sido interrumpido");
            }
        }
        System.out.println("Banco Virtual");
        System.out.println(version);
        System.out.printf("Saldo final: %.2f€\n", cb.consultarSaldo());
        System.out.println("Operaciones exitosas: " + exitosas.get());
        if (version.equalsIgnoreCase("con lock")) {
            System.out.println("Operaciones fallidas: " + fallidas.get());
        }
        System.out.println("Historial:");
        List<String> historial = cb.obtenerHistorial();
        for (int i = 0; i < historial.size(); i++) {
            System.out.println(historial.get(i));
        }
        System.out.println("Total operaciones: " + (exitosas.get() + fallidas.get() + ingresos.get()));

    }
}
