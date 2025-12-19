package br.com.iartes.salario.validacao;

public final class ValidadorEntrada {
    private ValidadorEntrada() {}

    public static void validarSalarioPositivo(double salarioBruto) {
        if (salarioBruto <= 0.00) {
            throw new IllegalArgumentException("Salário bruto deve ser positivo");
        }
    }

    public static void validarDependentesNaoNegativos(int numeroDependentes) {
        if (numeroDependentes < 0) {
            throw new IllegalArgumentException("Número de dependentes deve ser >= 0");
        }
    }
}
