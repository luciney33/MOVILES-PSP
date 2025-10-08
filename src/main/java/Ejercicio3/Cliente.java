package Ejercicio3;
import lombok.Data;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

@Data
public class Cliente implements Runnable{
    private final int id;
    private final Restaurante restaurante;
    private final BlockingQueue<Pedido> colaCocina;

    public Cliente(int id, Restaurante restaurante, BlockingQueue<Pedido> colaCocina) {
        this.id = id;
        this.restaurante = restaurante;
        this.colaCocina = colaCocina;
    }

    @Override
    public void run() {
        try {
            Plato[] platos = Plato.values();
            Plato platoElegido = platos[ThreadLocalRandom.current().nextInt(platos.length)];
            Pedido pedido = new Pedido(id, platoElegido);
            colaCocina.put(pedido);
            restaurante.registroPedido(pedido);
            System.out.println("--Cliente-" + id + " pide " + platoElegido.name());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
