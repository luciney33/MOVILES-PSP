package Ejercicio2;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CuentaBankSyn implements CuentaBankInterface{
    private CuentaBank cuentaBank;
    private double saldo;
    private List<String> historial = new ArrayList<>();
    public CuentaBankSyn(CuentaBank cuentaBank){
        this.cuentaBank = cuentaBank;
        saldo = cuentaBank.getSaldo();
    }

    @Override
    public synchronized boolean retirar(double cantidad) {
        boolean retirado = false;
        if (cantidad <= saldo) {
            saldo -= cantidad;
            historial.add(LocalDate.now()+ " RETIRO: " + cantidad + "€ | Saldo: " + saldo);
            retirado = true;
        }else {
            retirado = false;
            historial.add(LocalDate.now()+
                    " RETIRO FALLIDO: -" + cantidad + "€ | Fondos insuficientes | Saldo: " + saldo);
        }
        return retirado;
    }

    @Override
    public synchronized void ingresar(double cantidad) {
        historial.add(LocalDate.now()+
                " INGRESO: +" + cantidad + "€ | Saldo: " + saldo);
        saldo += cantidad;
    }

    @Override
    public synchronized double consultarSaldo() {
        historial.add(LocalDate.now()+
                " CONSULTA SALDO | Saldo actual: " + saldo);
        return saldo;
    }

    @Override
    public synchronized List<String> obtenerHistorial() {
        return historial;
    }
}
