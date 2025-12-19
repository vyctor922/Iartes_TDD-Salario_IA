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
}
