public class Hilos {
    public static void main(String[] args) {
        Thread hilo1 = new Thread(new Tarea("Hilo 1"));
        Thread hilo2 = new Thread(new Tarea("Hilo 2"));

        hilo1.start();
        hilo2.start();
    }

}
