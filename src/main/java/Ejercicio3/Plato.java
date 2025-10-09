package Ejercicio3;


public enum Plato {
    ENSALADA(2000),
    PASTA(3000),
    PIZZA(4000),
    CARNE(5000);

    private int tiempoMs;

    Plato(int tiempoMs) {
        this.tiempoMs = tiempoMs;
    }

    public int getTiempoMs() {
        return tiempoMs;
    }
}
