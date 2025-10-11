package Ejercicio5;
import lombok.Data;


@Data
public class Usuario implements Runnable {
    private final int id;
    private final TipoUsuario tipo;
    private final Servidor servidor;
    public Usuario(int id, TipoUsuario tipo, Servidor servidor) {
        this.id = id;
        this.tipo = tipo;
        this.servidor = servidor;
    }

    public void run(){
            TipoArchivo[] archivos = TipoArchivo.values();
            TipoArchivo archivoElegido = archivos[(int) (Math.random() * archivos.length)];
            Descarga descarga = new Descarga(id, tipo, archivoElegido);
            servidor.solicitarDescarga(descarga);
            System.out.println("Usuario " + id + ": " + tipo + " solicitó descargar " + archivoElegido);
    }
}
