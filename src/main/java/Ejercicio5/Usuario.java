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
        try {
            TipoArchivo[] archivos = TipoArchivo.values();
            TipoArchivo archivoElegido = archivos[(int) (Math.random() * archivos.length)];
            Descarga descarga = new Descarga(id, tipo, archivoElegido);
            servidor.solicitarDescargas(descarga);
            System.out.println("Usuario " + id + tipo +" inicia descarga de " + archivoElegido);
            Thread.sleep(archivoElegido.getTiempoMs());
            System.out.println("Usuario " + id + tipo +" finaliza descarga de " + archivoElegido);
            servidor.descargasCompletadas(descarga);
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }

    }
}
