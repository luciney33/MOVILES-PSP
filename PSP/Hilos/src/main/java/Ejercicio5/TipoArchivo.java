package Ejercicio5;

public enum TipoArchivo {
    DOCUMENTO(10),
    IMAGEN(50),
    VIDEO(  500),
    JUEGO(2000);

    private final int sizeMB;
    TipoArchivo(int sizeMB) {
        this.sizeMB = sizeMB;
    }
    public int getTiempoMs() {
        return sizeMB * 100;
    }
}
