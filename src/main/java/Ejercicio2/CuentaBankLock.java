package Ejercicio2;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CuentaBankLock implements CuentaBankInterface {
    private final Lock lock = new ReentrantLock();
    private CuentaBank cuentaBank;
    public CuentaBankLock(CuentaBank cuentaBank) {
        this.cuentaBank = cuentaBank;
    }

    @Override
    public boolean retirar(double cantidad) {
        boolean retirado = false;
        lock.lock();
        try {
            if (cantidad <= cuentaBank.getSaldo()) {
                cuentaBank.setSaldo(cuentaBank.getSaldo()-cantidad);
                cuentaBank.getHistorial().add(LocalTime.now()+ " RETIRO: " + String.format("%.2f€", cantidad) + " | SALDO: " + String.format("%.2f€", cuentaBank.getSaldo()));
                retirado = true;
            } else {
                retirado = false;
                cuentaBank.getHistorial().add(LocalTime.now() +
                        " RETIRO FALLIDO: " + String.format("%.2f€", cantidad) + " | Fondos insuficientes | SALDO: " + String.format("%.2f€", cuentaBank.getSaldo()));
            }
        } finally {
            lock.unlock();
        }
        return retirado;
    }

    @Override
    public void ingresar(double cantidad) {
        lock.lock();
        try {
            cuentaBank.setSaldo(cuentaBank.getSaldo()+cantidad);
            cuentaBank.getHistorial().add(LocalTime.now() +
                    " INGRESO: +" + String.format("%.2f€", cantidad) + " | SALDO: " + String.format("%.2f€", cuentaBank.getSaldo()));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public double consultarSaldo() {
        lock.lock();
        try {
            cuentaBank.getHistorial().add(LocalTime.now() +
                    " CONSULTA SALDO | SALDO ACTUAL: " + String.format("%.2f€", cuentaBank.getSaldo()));
            return cuentaBank.getSaldo();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<String> obtenerHistorial() {
        lock.lock();
        try {
            return cuentaBank.getHistorial();
        } finally {
            lock.unlock();
        }
    }
}
