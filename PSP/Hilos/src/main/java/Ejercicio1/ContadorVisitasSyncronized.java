package Ejercicio1;

public class ContadorVisitasSyncronized implements ContadorServiceInterface {
    private int contador = 0;

    public synchronized void incrementarVisitas() {//synchronized hace que solo un hilo pueda acceder a este metodo a la vez
        contador++;
    }
    public int getContador() {
        return contador;
    }
}
