package Ejercicio3;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class Pedido {
    private int idCliente;
    private Plato plato;
    private Mesa mesa;
    private long tiempoPreparacion;

    @Override
    public String toString() {
        return "Pedido" +"\n"+
                "Cliente= " + idCliente +
                "Plato= " + plato +
                "Mesa=" + mesa +
                "Tiempo de Preparacion=" + tiempoPreparacion;
    }
}
