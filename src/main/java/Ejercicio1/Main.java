package Ejercicio1;

public class Main {
    public static void main(String[] args) {
        ContadorVisitasService cc1 = new ContadorVisitasService(new ContadorVisitas());
        cc1.work(Constantes.SIN_SYNC);

        ContadorVisitasService cc2 = new ContadorVisitasService(new ContadorVisitasSyncronized());
        cc2.work(Constantes.CON_SYNC);

        ContadorVisitasService cc3 = new ContadorVisitasService(new ContadorVisitasAtomic());
        cc3.work(Constantes.CON_ATOMIC);
    }
}
