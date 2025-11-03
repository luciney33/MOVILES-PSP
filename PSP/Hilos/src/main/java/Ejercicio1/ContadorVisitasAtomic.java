package Ejercicio1;

import java.util.concurrent.atomic.AtomicInteger;

public class ContadorVisitasAtomic implements ContadorServiceInterface{
    private AtomicInteger contador = new AtomicInteger(0);//AtomicInteger es una clase que permite incrementar un entero de forma atomica
    public void incrementarVisitas() {
        contador.incrementAndGet();
    }
    public int getContador() {
        return contador.get();
    }
}
