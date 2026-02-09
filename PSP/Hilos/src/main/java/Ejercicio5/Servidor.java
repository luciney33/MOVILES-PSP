package Ejercicio5;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Servidor {

    private final int numUsuarios = 50;
    private final int maxDescargas = 5;

    private final PriorityBlockingQueue<Descarga> colaDescarga = new PriorityBlockingQueue<>();
    private final AtomicInteger descargasTotales = new AtomicInteger(0);
    private final AtomicInteger descargasCompletadas = new AtomicInteger(0);
    private final AtomicInteger descargasFallidas = new AtomicInteger(0);

    private final ExecutorService executor = Executors.newFixedThreadPool(maxDescargas);
    private final Random random = new Random();

    public void solicitarDescarga(Descarga descarga) {
        colaDescarga.add(descarga);
        descargasTotales.incrementAndGet();
        System.out.println(LocalTime.now() + " Usuario " + descarga.getIdUsuario() +
                " solicitó descarga de " + descarga.getTipoArchivo());
    }

    private CompletableFuture<Void> intentarDescarga(Descarga descarga, int intento) {
        return CompletableFuture.runAsync(() -> {
                    try {
                        if (random.nextDouble() < 0.2) throw new RuntimeException("Fallo en descarga");
                        Thread.sleep(descarga.getTipoArchivo().getTiempoMs());
                        System.out.println(LocalTime.now() + " Descarga completada: Usuario " +
                                descarga.getIdUsuario() + " Archivo: " + descarga.getTipoArchivo());
                        descargasCompletadas.incrementAndGet();

                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Descarga interrumpida");
                    }
                }, executor)
                .handle((v, ex) -> {
                    if (ex == null) {
                        return CompletableFuture.<Void>completedFuture(null);
                    } else {
                        if (intento < 3) {
                            System.out.println(LocalTime.now() + " Fallo en intento " + intento +
                                    " de usuario " + descarga.getIdUsuario() + ". Reintentando...");
                            return intentarDescarga(descarga, intento + 1);
                        } else {
                            System.out.println(LocalTime.now() + " Descarga fallida tras 3 intentos: Usuario "
                                    + descarga.getIdUsuario() + " Archivo: " + descarga.getTipoArchivo());
                            descargasFallidas.incrementAndGet();
                            return CompletableFuture.<Void>completedFuture(null);
                        }
                    }
                })
                .thenCompose(cf -> cf);
    }

    public void iniciar() throws InterruptedException {
        Thread[] hilosUsuarios = new Thread[numUsuarios];
        for (int i = 0; i < numUsuarios; i++) {
            TipoUsuario tipo = TipoUsuario.values()[ (int) (Math.random() * TipoUsuario.values().length) ];
            Usuario usuario = new Usuario(i + 1, tipo, this);
            hilosUsuarios[i] = new Thread(usuario);
            hilosUsuarios[i].start();
        }

        for (int i = 0; i < hilosUsuarios.length; i++) {
            hilosUsuarios[i].join();
        }

        int total = descargasTotales.get();
        List<CompletableFuture<Void>> futures = new ArrayList<>(total);

        for (int i = 0; i < total; i++) {
            Descarga d = colaDescarga.take();
            CompletableFuture<Void> fut = intentarDescarga(d, 1);
            futures.add(fut);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
        mostrarEstadisticas();
    }

    private void mostrarEstadisticas() {
        System.out.println("---ESTADÍSTICAS DEL SERVIDOooR -----");
        System.out.println("Descargas totales: " + descargasTotales.get());
        System.out.println("Descargas completadas: " + descargasCompletadas.get());
        System.out.println("Descargas fallidas: " + descargasFallidas.get());
    }
}