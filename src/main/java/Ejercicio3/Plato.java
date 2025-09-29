package Ejercicio3;

import lombok.Data;

public enum Plato {
    ENSALADA(2000),    // 2 segundos
    PASTA(3000),       // 3 segundos
    PIZZA(4000),       // 4 segundos
    CARNE(5000);       // 5 segundos

    private int tiempoMs;
    Plato(int tiempoMs) {
        this.tiempoMs = tiempoMs;
    }
    public int getTiempoMs() {
        return tiempoMs;
    }
    public Plato randomPlato() {
        Plato[] platos = values();
        int indice = (int) (Math.random() * platos.length);
        return platos[indice];
    }
}
