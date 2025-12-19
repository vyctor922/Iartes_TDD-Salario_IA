package br.com.iartes.salario;

public class CalculadoraSalarioLiquido {

    public double calcular(double salarioBruto) {
        validarEntrada(salarioBruto);
        double inss = Math.min(0.08 * salarioBruto, 500.00);
        double irrf = salarioBruto > 2000.00 ? 0.10 * salarioBruto : 0.00;
        double liquido = salarioBruto - inss - irrf;
        return arredondar2(liquido);
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

