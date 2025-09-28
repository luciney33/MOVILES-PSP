package Ejercicio3;

import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;
@Data
public class Cocinero {
    private Cocinero[] cocineros = new Cocinero[3];
    private String nombre;
    AtomicInteger platosServidos = new AtomicInteger();
    public void servirPlato() {
        platosServidos.incrementAndGet();
    }
}
