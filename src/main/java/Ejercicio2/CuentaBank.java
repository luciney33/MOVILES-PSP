package Ejercicio2;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CuentaBank {
    private double saldo = 10000.00;
    private List<String> historial = new ArrayList<>();
}
