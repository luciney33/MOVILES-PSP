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
        AtomicInteger exitosas = new AtomicInteger(0);
        AtomicInteger fallidas = new AtomicInteger(0);
        for (int i = 0; i < 50; i++) {
            Thread hilo = new Thread(() -> {
                try {
                    for (int j = 0; j < 10; j++) {
                        double prob = Math.random();
                        if (prob <= 0.6) {
                            double cantidadRetiro = 1 + Math.random() * 99;
                            boolean exito = cb.retirar(cantidadRetiro);
                            if (exito) exitosas.incrementAndGet();
                            else fallidas.incrementAndGet();
                        } else {
                            double cantidadIngreso = 1 + Math.random() * 49;
                            cb.ingresar(cantidadIngreso);
                        }
                        Thread.sleep((int) (Math.random() * 201) + 100);
                    }

                } catch (InterruptedException e) {
                    System.out.println("El hilo ha sido interrumpido");
                }
            });
            hilos[i] = hilo;
            hilo.start();
        }
        for (int i = 0; i < 50; i++) {
            try {
                hilos[i].join();
            } catch (InterruptedException e) {
                System.out.println("El hilo ha sido interrumpido");
            }
        }
        System.out.println("BANCO VIRTUAL (" + version + ")");
        System.out.printf("Saldo final: %.2f€\n", cb.consultarSaldo());
        System.out.println("Operaciones exitosas: " + exitosas.get());
        System.out.println("Operaciones fallidas: " + fallidas.get());
        System.out.println("Historial de operaciones:");
        List<String> historial = cb.obtenerHistorial();
        for (int i = 0; i < historial.size(); i++) {
            System.out.println(historial.get(i));
        }
    }
}
