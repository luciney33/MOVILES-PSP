package Ejercicio1;

public class ContadorVisitasSyncronized {//AtomicInteger es una clase que permite incrementar un entero de forma atomica
    private int contador = 0;

    public synchronized void incrementarVisitas() {//synchronized hace que solo un hilo pueda acceder a este metodo a la vez
        contador++;
    }
    public int getContador() {
        return contador;
    }
}
