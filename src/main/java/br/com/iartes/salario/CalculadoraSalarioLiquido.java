package br.com.iartes.salario;

import br.com.iartes.salario.util.Arredondador;
import br.com.iartes.salario.validacao.ValidadorEntrada;

public class CalculadoraSalarioLiquido {

    private final CalculadoraDescontos calculadoraDescontos;

    public CalculadoraSalarioLiquido() {
        this(new CalculadoraDescontos());
    }

    public CalculadoraSalarioLiquido(CalculadoraDescontos calculadoraDescontos) {
        this.calculadoraDescontos = calculadoraDescontos;
    }

    public double calcular(double salarioBruto) {
        ValidadorEntrada.validarSalarioPositivo(salarioBruto);
        double inss = calculadoraDescontos.calcularINSS(salarioBruto);
        double irrf = calculadoraDescontos.calcularIRRF(salarioBruto);
        double liquido = salarioBruto - inss - irrf;
        return Arredondador.duasCasas(liquido);
    }

    public double calcular(double salarioBruto, int numeroDependentes, boolean optanteValeTransporte) {
        ValidadorEntrada.validarSalarioPositivo(salarioBruto);
        if (numeroDependentes < 0) {
            throw new IllegalArgumentException("Número de dependentes deve ser >= 0");
        }
        double inss = calculadoraDescontos.calcularINSS(salarioBruto);
        double irrf;
        if (numeroDependentes == 0 && !optanteValeTransporte) {
            irrf = calculadoraDescontos.calcularIRRF(salarioBruto);
        } else {
            irrf = calculadoraDescontos.calcularIRRF(salarioBruto, numeroDependentes);
        }
        double vt = optanteValeTransporte ? Arredondador.duasCasas(0.06 * salarioBruto) : 0.00;
        double liquido = salarioBruto - inss - irrf - vt;
        return Arredondador.duasCasas(liquido);
    }
}
