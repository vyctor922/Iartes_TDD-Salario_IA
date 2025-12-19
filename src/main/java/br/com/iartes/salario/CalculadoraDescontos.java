package br.com.iartes.salario;

public class CalculadoraDescontos {

    public double calcularINSS(double salarioBruto) {
        validarEntrada(salarioBruto);
        double desconto = 0.08 * salarioBruto;
        desconto = Math.min(desconto, 500.00);
        return arredondar2(desconto);
    }

    public double calcularIRRF(double salarioBruto) {
        validarEntrada(salarioBruto);
        double desconto = salarioBruto > 2000.00 ? 0.10 * salarioBruto : 0.00;
        return arredondar2(desconto);
    }

    private void validarEntrada(double salarioBruto) {
        if (salarioBruto <= 0.00) {
            throw new IllegalArgumentException("Salário bruto deve ser positivo");
        }
    }

    private double arredondar2(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}

