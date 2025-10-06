package Ejercicio4;


public enum TipoVehiculo {
        NORMAL(1.0),  // 1€/minuto
        VIP(2.0);

        private final double tarifaPorMinuto;

        TipoVehiculo(double tarifaPorMinuto) {
        this.tarifaPorMinuto = tarifaPorMinuto;
        }
        public double getTarifaPorMinuto() {
        return tarifaPorMinuto;
        }
}
