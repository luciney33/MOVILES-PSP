package Ejercicio5;

import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;


public class Servidor {
    private int numUsuarios;
    private final PriorityBlockingQueue<Descarga> colaDescarga = new PriorityBlockingQueue<>();
    private AtomicInteger descargasCompletadas = new AtomicInteger(0);
    private AtomicInteger descargasFallidas = new AtomicInteger(0);
    private AtomicInteger descargasTotales = new AtomicInteger(0);


    public void solicitarDescargas(Descarga descarga) {
        colaDescarga.add(descarga);
        descargasTotales.incrementAndGet();
    }

    public void descargasCompletadas(Descarga descarga) {
        descargasCompletadas.incrementAndGet();
    }

    public void hacerDescarga(Descarga descarga, int intento) throws InterruptedException {
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(descarga.getTipoArchivo().getTiempoMs());
                System.out.println("Descarga completada: Usuario " + descarga.getIdUsuario() +
                        " Tipo: " + descarga.getTipoUsuario() +
                        " Archivo: " + descarga.getTipoArchivo());
                descargasCompletadas(descarga);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).exceptionally(ex -> {
            descargasFallidas.incrementAndGet();
            System.out.println("Descarga fallida: Usuario " + descarga.getIdUsuario() +
                    " Tipo: " + descarga.getTipoUsuario() +
                    " Archivo: " + descarga.getTipoArchivo());
            return null;
        });
    }

    public void iniciar()throws InterruptedException{
        int numDescargas = 5;
        ExecutorService executor = Executors.newFixedThreadPool(numDescargas);
        for (int i = 0; i < numDescargas; i++) {
            executor.submit(() -> {
                try {
                    while (true) {
                        Descarga descarga = colaDescarga.take();

                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

    }
}
