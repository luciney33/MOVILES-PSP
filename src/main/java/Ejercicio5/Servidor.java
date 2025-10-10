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

    // Usuario llama a esto para encolar la descarga
    public void solicitarDescarga(Descarga descarga) {
        colaDescarga.add(descarga);
        descargasTotales.incrementAndGet();
        System.out.println(LocalTime.now() + " Usuario " + descarga.getIdUsuario() +
                " solicitó descarga de " + descarga.getTipoArchivo());
    }

    // intento asíncrono que ejecuta la descarga sobre downloadExecutor
    private CompletableFuture<Void> intentarDescarga(Descarga descarga, int intento) {
        // Ejecutar la simulación en el executor (limita concurrencia)
        return CompletableFuture.runAsync(() -> {
                    // Simulación de descarga (puede lanzar RuntimeException para simular fallo)
                    try {
                        // Simulamos fallo aleatorio (20%)
                        if (random.nextDouble() < 0.2) throw new RuntimeException("Fallo en descarga");

                        // Simular tiempo de descarga según el tipo de archivo
                        Thread.sleep(descarga.getTipoArchivo().getTiempoMs());

                        // Éxito: actualizar contadores e imprimir
                        System.out.println(LocalTime.now() + " Descarga completada: Usuario " +
                                descarga.getIdUsuario() + " Archivo: " + descarga.getTipoArchivo());
                        descargasCompletadas.incrementAndGet();

                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Descarga interrumpida");
                    }
                }, executor)
                // handle para detectar si hubo excepción y decidir reintento
                .handle((v, ex) -> {
                    if (ex == null) {
                        // éxito: devolvemos un CompletableFuture ya completado
                        return CompletableFuture.<Void>completedFuture(null);
                    } else {
                        // hubo fallo
                        if (intento < 3) {
                            System.out.println(LocalTime.now() + " Fallo en intento " + intento +
                                    " de usuario " + descarga.getIdUsuario() + ". Reintentando...");
                            // devolvemos la futura del reintento (se encadenará más abajo)
                            return intentarDescarga(descarga, intento + 1);
                        } else {
                            // último intento fallido
                            System.out.println(LocalTime.now() + " Descarga fallida tras 3 intentos: Usuario "
                                    + descarga.getIdUsuario() + " Archivo: " + descarga.getTipoArchivo());
                            descargasFallidas.incrementAndGet();
                            return CompletableFuture.<Void>completedFuture(null);
                        }
                    }
                })
                // thenCompose para "aplanar" CompletableFuture<CompletableFuture<Void>> -> CompletableFuture<Void>
                .thenCompose(cf -> cf);
    }

    // Inicia: lanza usuarios, espera que encolen todas las descargas y despacha las tareas al executor
    public void iniciar() throws InterruptedException {
        // 1) Lanzar usuarios (puedes usar hilos virtuales si los tienes, aquí hilos normales)
        Thread[] hilosUsuarios = new Thread[numUsuarios];
        for (int i = 0; i < numUsuarios; i++) {
            TipoUsuario tipo = TipoUsuario.values()[ (int) (Math.random() * TipoUsuario.values().length) ];
            Usuario usuario = new Usuario(i + 1, tipo, this);
            hilosUsuarios[i] = new Thread(usuario);
            hilosUsuarios[i].start();
        }

        // 2) Esperar a que todos los usuarios terminen de generar sus solicitudes
        for (int i = 0; i < hilosUsuarios.length; i++) {
            hilosUsuarios[i].join();
        }

        // 3) Dispatcher: tomar todas las descargas encoladas y lanzar sus CompletableFutures
        int total = descargasTotales.get();
        List<CompletableFuture<Void>> futures = new ArrayList<>(total);

        for (int i = 0; i < total; i++) {
            Descarga d = colaDescarga.take(); // bloquea hasta que haya una descarga
            // lanzar los intentos asíncronos sobre downloadExecutor mediante intentarDescarga
            CompletableFuture<Void> fut = intentarDescarga(d, 1);
            futures.add(fut);
        }

        // 4) Esperar a que todas las descargas (con sus reintentos) terminen
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // 5) Cerrar executor y mostrar estadísticas
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        mostrarEstadisticas();
    }

    private void mostrarEstadisticas() {
        System.out.println("\n--- ESTADÍSTICAS DEL SERVIDOR ---");
        System.out.println("Descargas totales: " + descargasTotales.get());
        System.out.println("Descargas completadas: " + descargasCompletadas.get());
        System.out.println("Descargas fallidas: " + descargasFallidas.get());
        System.out.println("---------------------------------");
    }
}