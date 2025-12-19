package br.com.iartes.salario.validacao;

public final class ValidadorEntrada {
    private ValidadorEntrada() {}

    public static void validarSalarioPositivo(double salarioBruto) {
        if (salarioBruto <= 0.00) {
            throw new IllegalArgumentException("Salário bruto deve ser positivo");
        }
    }
}

