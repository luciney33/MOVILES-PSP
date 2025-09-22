package Ejercicio1;

public class ContadorVisitasService  {
    private ContadorServiceInterface cc;

    public ContadorVisitasService(ContadorServiceInterface cc) {
        this.cc = cc;
    }

    public void work(String version) {
            Thread[] hilos = new Thread[1000];
            for (int i = 0; i < 1000; i++) {
                Thread hilo = new Thread(()->{
                    // numero aleatorio de tiempo entre 50 y 150
                    try {
                        Thread.sleep((int)(Math.random() * 100) + 50);
                    } catch (InterruptedException e) {
                        System.out.println("El hilo ha sido interrumpido");
                    }
                    cc.incrementarVisitas();
                });
                hilos[i] = hilo;
                hilo.start();
            }
            for (int i = 0; i < 1000; i++) {
                try {
                    hilos[i].join();
                } catch (InterruptedException e) {
                    System.out.println("El hilo ha sido interrumpido");
                }
            }
        System.out.println(version);
            System.out.println("Contador: " + cc.getContador());

    }

}

