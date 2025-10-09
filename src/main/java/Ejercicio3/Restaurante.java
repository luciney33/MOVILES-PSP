package Ejercicio3;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Restaurante {
    private final int numClientes = 100;
    private final int numCocineros = 3;
    private final BlockingQueue<Pedido> colaCocina = new LinkedBlockingQueue<>(10);
    private final AtomicInteger clTotales = new AtomicInteger(0);
    private final AtomicInteger clAtendidos = new AtomicInteger(0);

    public void registroPedido(Pedido pedido) {
        clTotales.incrementAndGet();
    }

    public void registroCocinero(Pedido pedido) {
        clAtendidos.incrementAndGet();
    }


    public void iniciarServicio() throws InterruptedException {
        System.out.println("RESTAURANTE CONCURRENTE");
        ExecutorService executor = Executors.newCachedThreadPool();

        for (int i = 1; i <= numCocineros; i++) {
            executor.submit(new Cocinero(i, this, colaCocina));
        }
        for (int i = 1; i <= numClientes; i++) {
            executor.submit(new Cliente(i, this, colaCocina)); Thread.sleep(500);
        }
        while (!colaCocina.isEmpty()) {
            Thread.sleep(1000);
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        mostrarEstadisticas();
    }
    private void mostrarEstadisticas() {
        System.out.println("Estadísticas del Restaurante");
        System.out.println("Clientes totales: " + clTotales.get());
        System.out.println("Clientes atendidos: " + clAtendidos.get()+ "/" +clTotales.get());
    }
}
