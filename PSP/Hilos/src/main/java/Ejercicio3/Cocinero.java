package Ejercicio3;
import lombok.Data;
import java.util.concurrent.BlockingQueue;


@Data
public class Cocinero implements Runnable {
    private final int id;
    private final Restaurante restaurante;
    private final BlockingQueue<Pedido> colaCocina;

    public Cocinero(int id, Restaurante restaurante, BlockingQueue<Pedido> colaCocina) {
        this.id = id;
        this.restaurante = restaurante;
        this.colaCocina = colaCocina;
    }

    @Override
    public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Pedido pedido = colaCocina.take();
                    System.out.println("Cocinero " + id + " prepara " + pedido);
                    Thread.sleep(pedido.getPlato().getTiempoMs());
                    restaurante.registroCocinero(pedido);
                    System.out.println("Cocinero " + id + " terminó " + pedido);
                }
            } catch (InterruptedException e) {
                System.out.println("Cocinero " + id + " terminó su turno");
                Thread.currentThread().interrupt();
            }
    }
}
