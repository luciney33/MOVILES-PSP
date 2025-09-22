package Ejercicio2;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CuentaBankLock implements CuentaBankInterface {
    private final Lock lock = new ReentrantLock();
    private double saldo = 10000;
    private List<String> historial = new ArrayList<>();

    @Override
    public boolean retirar(double cantidad) {
        boolean retirado = false;
        lock.lock();
        try {
            if (cantidad <= saldo) {
                saldo -= cantidad;
                historial.add(LocalDate.now() + "RETIRO: " + cantidad + "€ | Saldo: " + saldo);
                retirado = true;
            } else {
                retirado = false;
                historial.add(LocalDate.now() +
                        "RETIRO FALLIDO: " + cantidad + "€ | Fondos insuficientes | Saldo: " + saldo);
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
            saldo += cantidad;
            historial.add(LocalDate.now() +
                    "INGRESO: +" + cantidad + "€ | Saldo: " + saldo);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public double consultarSaldo() {
        lock.lock();
        try {
            historial.add(LocalDate.now() +
                    "CONSULTA SALDO | Saldo actual: " + saldo);
            return saldo;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<String> obtenerHistorial() {
        lock.lock();
        try {
            return historial;
        } finally {
            lock.unlock();
        }
    }
}
