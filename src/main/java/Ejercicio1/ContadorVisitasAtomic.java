package Ejercicio1;

import java.util.concurrent.atomic.AtomicInteger;

public class ContadorVisitasAtomic {
    private AtomicInteger contador = new AtomicInteger(0);
    public void incrementarVisitas() {
        contador.incrementAndGet();
    }
    public int getContador() {
        return contador.get();
    }
}
