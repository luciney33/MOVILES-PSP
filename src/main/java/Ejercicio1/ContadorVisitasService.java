package Ejercicio1;

public class ContadorVisitasService  {
    private ContadorServiceInterface cc;
    public ContadorServiceInterface getCc() {
        return cc;
    }

    public void setCc(ContadorServiceInterface cc) {
        this.cc = cc;
    }
    public ContadorVisitasService(ContadorServiceInterface cc) {
        this.cc = cc;
    }

    public void work() {
            Thread[] hilos = new Thread[1000];
            for (int i = 0; i < 1000; i++) {
                Thread hilo = new Thread(()->{
                    // numero aleatorio entre 50 y 150
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
            System.out.println("Contador2: " + cc.getContador());
        }



    @Override
    public void incrementarVisitas() {

    }

    @Override
    public int getContador() {
        return 0;
    }

}

