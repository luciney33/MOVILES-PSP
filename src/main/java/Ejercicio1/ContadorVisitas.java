package Ejercicio1;

public class ContadorVisitas implements ContadorServiceInterface{
    private int contador;
    @Override
    public void incrementarVisitas() {
        contador++;
    }

    @Override
    public int getContador() {
        return contador;
    }

}

