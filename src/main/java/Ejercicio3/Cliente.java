package Ejercicio3;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.logging.Logger;

@Data
@AllArgsConstructor
public class Cliente implements Runnable{
    private int idCliente;
    private Mesa mesa;
    private final Logger log = Logger.getLogger(Cliente.class.getName());


    public void run(){
        try {
            Pedido pedido = new Pedido(idCliente);
            mesa.ponerPedido(pedido);
            pedido.getLatch().await();
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
            log.info(" Cliente " + idCliente + " parado");
        }

    }
}
