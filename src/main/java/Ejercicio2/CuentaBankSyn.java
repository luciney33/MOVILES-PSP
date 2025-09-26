package Ejercicio2;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CuentaBankSyn implements CuentaBankInterface{
    private CuentaBank cuentaBank;
    public CuentaBankSyn(CuentaBank cuentaBank) {
        this.cuentaBank = cuentaBank;
    }


    @Override
    public synchronized boolean retirar(double cantidad) {
        boolean retirado = false;
        if (cantidad <= cuentaBank.getSaldo()) {
            cuentaBank.setSaldo(cuentaBank.getSaldo() - cantidad);
            cuentaBank.getHistorial().add(LocalDate.now() + " RETIRO: " + String.format("%.2f€", cantidad) + " | SALDO: " + String.format("%.2f€", cuentaBank.getSaldo()));
            retirado = true;
        } else {
            cuentaBank.getHistorial().add(LocalDate.now() + " RETIRO FALLIDO: " + String.format("%.2f€", cantidad) + " | Fondos insuficientes | SALDO: " + String.format("%.2f€", cuentaBank.getSaldo()));
        }
        return retirado;
    }

    @Override
    public synchronized void ingresar(double cantidad) {
        cuentaBank.setSaldo(cuentaBank.getSaldo() + cantidad);
        cuentaBank.getHistorial().add(LocalDate.now() + " INGRESO: +" + String.format("%.2f€", cantidad) + " | SALDO: " + String.format("%.2f€", cuentaBank.getSaldo()));
    }

    @Override
    public synchronized double consultarSaldo() {
        cuentaBank.getHistorial().add(LocalDate.now() + " CONSULTA SALDO | SALDO ACTUAL: " + String.format("%.2f€", cuentaBank.getSaldo()));
        return cuentaBank.getSaldo();
    }

    @Override
    public synchronized List<String> obtenerHistorial() {
        return cuentaBank.getHistorial();
    }
}
