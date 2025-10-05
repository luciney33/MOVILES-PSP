package Ejercicio3;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;

@Data
public class Pedido {
    private int idCliente;
    private Plato plato;
    private long tiempoPreparacion;
    private CountDownLatch latch = new CountDownLatch(1);

    public Pedido() {}
    public Pedido(int idCliente) {
        this.idCliente = idCliente;
        Plato[] platos = Plato.values();
        this.plato = platos[(int)(Math.random() * platos.length)];
    }
}
