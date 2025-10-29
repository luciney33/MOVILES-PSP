package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Estadistica {
    private static Map<String, Estadistica> estadisticasUsuarios = new HashMap<>();
    private List<Partida> partidas;

    public Estadistica() {
        this.partidas = new ArrayList<>();
    }


    public int getTotalPartidas() {
        return partidas.size();
    }
    public int getPuntuacionTotal() {
        return partidas.stream()
                .mapToInt(Partida::getPuntuacion)
                .sum();
    }

    public long getTiempoTotalSegundos() {
        return partidas.stream()
                .mapToLong(Partida::getTiempoJuego)
                .sum() / 1000;
    }

    public void agregarPartida(Partida partida) {
        this.partidas.add(partida);
    }

    public static Estadistica cogerEstadistica(String usuario) {
        return estadisticasUsuarios.get(usuario);
    }

    public static void crearEstadistica(String usuario) {
        estadisticasUsuarios.putIfAbsent(usuario, new Estadistica());
    }

    public static Estadistica cogerCrearEstadistica(String usuario) {
        crearEstadistica(usuario);
        return cogerEstadistica(usuario);
    }
    public static Map<String, Estadistica> todasEstadisticas() {
        return new HashMap<>(estadisticasUsuarios);
    }
}
