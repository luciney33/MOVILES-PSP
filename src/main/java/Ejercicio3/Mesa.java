package Ejercicio3;

import lombok.Data;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

@Data
public class Mesa {
    private BlockingQueue<Pedido> pedidos = new ArrayBlockingQueue<>(10);
    private AtomicInteger totalPedidos = new AtomicInteger(0);
    private AtomicInteger vecesLlena = new AtomicInteger(0);
    private final Logger log = Logger.getLogger(Mesa.class.getName());

    public void ponerPedido(Pedido pedido) throws InterruptedException {
        boolean añadido = pedidos.offer(pedido);
        if (!añadido) {
            vecesLlena.incrementAndGet();
            pedidos.put(pedido);
        }
        totalPedidos.incrementAndGet();
        String tiempo= LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        log.info(tiempo+" Pedido de Cliente"+pedido.getIdCliente()+": "+pedido.getPlato());
    }

    public Pedido sacarPedido() throws InterruptedException {
        Pedido pedido = pedidos.take();
        String tiempo= LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        log.info(tiempo+" Cocinero coje pedido de Cliente"+pedido.getIdCliente()+": "+pedido.getPlato()+"\n");
        return pedido;
    }
}
