package br.com.iartes.salario;

import br.com.iartes.salario.util.Arredondador;
import br.com.iartes.salario.validacao.ValidadorEntrada;

public class CalculadoraDescontos {

    public double calcularINSS(double salarioBruto) {
        ValidadorEntrada.validarSalarioPositivo(salarioBruto);
        double desconto = RegrasTributarias.ALIQUOTA_INSS * salarioBruto;
        desconto = Math.min(desconto, RegrasTributarias.TETO_INSS);
        return Arredondador.duasCasas(desconto);
    }

    public double calcularIRRF(double salarioBruto) {
        ValidadorEntrada.validarSalarioPositivo(salarioBruto);
        double desconto = salarioBruto > RegrasTributarias.LIMITE_ISENCAO_IRRF
                ? RegrasTributarias.ALIQUOTA_IRRF * salarioBruto
                : 0.00;
        return Arredondador.duasCasas(desconto);
    }
}
