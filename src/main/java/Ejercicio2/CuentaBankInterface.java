package Ejercicio2;

import java.util.List;

public interface CuentaBankInterface {
    public boolean retirar(double cantidad);
    public void ingresar(double cantidad);
    public double consultarSaldo();
    public List<String> obtenerHistorial();
}
