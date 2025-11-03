package Ejercicio3;
import lombok.Data;


@Data
public class Pedido {
    private final int id;
    private final Plato plato;

    public Pedido(int id, Plato plato) {
        this.id = id;
        this.plato = plato;
    }

}
