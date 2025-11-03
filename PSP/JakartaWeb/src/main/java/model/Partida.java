package model;

public class Partida {
    private int puntuacion;
    private int intentos;
    private long tiempoJuego;

    public Partida(int puntuacion, int intentos, long tiempoJuego) {
        this.puntuacion = puntuacion;
        this.intentos = intentos;
        this.tiempoJuego = tiempoJuego;
    }

    public int getPuntuacion() { return puntuacion; }
    public int getIntentos() { return intentos; }
    public long getTiempoJuego() { return tiempoJuego; }
}
