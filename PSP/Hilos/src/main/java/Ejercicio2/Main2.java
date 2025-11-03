package Ejercicio2;


public class Main2 {
    public static void main(String[] args) {
        CuentaBank cuentaLock = new CuentaBank();
        CuentaBankService cb1 = new CuentaBankService(new CuentaBankLock(cuentaLock));
        cb1.work(Constantes.CON_LOCK);

        CuentaBank cuentaSync = new CuentaBank();
        CuentaBankService cb2 = new CuentaBankService(new CuentaBankSyn(cuentaSync));
        cb2.work(Constantes.CON_SYNC);
    }
}
