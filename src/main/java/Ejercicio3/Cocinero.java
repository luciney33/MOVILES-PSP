package Ejercicio3;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

@Data
@AllArgsConstructor
public class Cocinero implements Runnable {
    private int idCocinero;
    private Mesa mesa;
    private final Logger log = Logger.getLogger(Cocinero.class.getName());

    public void run() {
        while (true) {
            try {

                Pedido pedido = mesa.sacarPedido();

                if (pedido.getIdCliente() == -1) {
                    break;
                }

                String tiempo = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                log.info(tiempo + " Cocinero" + idCocinero + " termina"
                        + pedido.getPlato() + " para Cliente" + pedido.getIdCliente());

                pedido.getLatch().countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.info(" Cocinero" + idCocinero + " parado");
            }
        }
    }
}
