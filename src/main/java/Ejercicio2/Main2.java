package Ejercicio2;


public class Main2 {
    public static void main(String[] args) {
        CuentaBankService cb1 = new CuentaBankService(new CuentaBankLock());
        cb1.work(Constantes.CON_LOCK);

        CuentaBankService cb2 = new CuentaBankService(new CuentaBankSyn());
        cb2.work(Constantes.CON_SYNC);
    }
}
