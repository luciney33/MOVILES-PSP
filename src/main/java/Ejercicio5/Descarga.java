package Ejercicio5;

import lombok.Data;

@Data
public class Descarga implements Comparable<Descarga> {
    private final int idUsuario;
    private final TipoUsuario tipoUsuario;
    private final TipoArchivo tipoArchivo;

    public Descarga(int idUsuario, TipoUsuario tipoUsuario, TipoArchivo tipoArchivo) {
        this.idUsuario = idUsuario;
        this.tipoUsuario = tipoUsuario;
        this.tipoArchivo = tipoArchivo;
    }
    @Override
    public int compareTo(Descarga otra) {
        if (this.tipoUsuario != otra.tipoUsuario) {
            if (this.tipoUsuario == TipoUsuario.PREMIUM) {
                return -1;
            } else {
                return 1;
            }
        }
        return Integer.compare(this.idUsuario, otra.idUsuario);
    }

}
