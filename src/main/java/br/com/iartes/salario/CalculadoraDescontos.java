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

    public double calcularIRRF(double salarioBruto, int numeroDependentes) {
        ValidadorEntrada.validarSalarioPositivo(salarioBruto);
        if (numeroDependentes < 0) {
            throw new IllegalArgumentException("Número de dependentes deve ser >= 0");
        }
        double aliquotaBase;
        if (salarioBruto <= RegrasTributarias.LIMITE_ISENCAO_IRRF) {
            aliquotaBase = 0.00;
        } else if (salarioBruto < RegrasTributarias.LIMITE_PROGRESSAO_IRRF_SUPERIOR) {
            aliquotaBase = RegrasTributarias.ALIQUOTA_IRRF;
        } else {
            aliquotaBase = RegrasTributarias.ALIQUOTA_IRRF_FAIXA_SUPERIOR;
        }
        double irBase = aliquotaBase * salarioBruto;
        double deducao = numeroDependentes * RegrasTributarias.DEDUCAO_POR_DEPENDENTE;
        double irFinal = Math.max(0.00, irBase - deducao);
        return Arredondador.duasCasas(irFinal);
    }
}
