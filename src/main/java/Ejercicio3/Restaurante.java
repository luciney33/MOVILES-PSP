package Ejercicio3;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Restaurante {
    public static void main(String[] args) throws InterruptedException {
        int numClientes = 100;
        int numCocineros = 3;
        final Logger log = Logger.getLogger(Restaurante.class.getName());

        log.info("=== RESTAURANTE   ===");
        log.info("Iniciando servicio con " + numCocineros + " cocineros y " + numClientes + " clientes");

        Mesa mesa = new Mesa();

        List<Thread> hilosCocineros = new ArrayList<>();
        for (int i = 1; i <= numCocineros; i++) {
            Cocinero cocinero = new Cocinero(i, mesa);
            Thread hiloCocinero = new Thread(cocinero);
            hiloCocinero.start();
            hilosCocineros.add(hiloCocinero);
        }

        List<Thread> hilosClientes = new ArrayList<>();
        for (int i = 1; i <= numClientes; i++) {
            Cliente cliente = new Cliente(i,mesa);
            Thread hiloCliente = new Thread(cliente);
            hiloCliente.start();
            hilosClientes.add(hiloCliente);

            Thread.sleep(0);
        }

        for (int i = 0; i < hilosClientes.size(); i++) {
            hilosClientes.get(i).join();
        }

        for (int i = 0; i < numCocineros; i++) {
            Pedido prueba = new Pedido();
            prueba.setIdCliente(-1);
            mesa.ponerPedido(prueba);
        }

        for (int i = 0; i < hilosCocineros.size(); i++) {
            hilosCocineros.get(i).join();
        }
        int pedidosNormales = mesa.getTotalPedidos().get() - numCocineros;

        log.info("--- ESTADÍSTICAS FINALES ---");
        log.info("Clientes atendidos: " + pedidosNormales + "/" + numClientes);
        log.info("Platos servidos: " + pedidosNormales);
        log.info("Mesa llena (veces): " + mesa.getVecesLlena().get());
    }
}
