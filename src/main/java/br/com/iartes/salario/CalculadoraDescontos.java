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
        ValidadorEntrada.validarDependentesNaoNegativos(numeroDependentes);
        double irBase = aliquotaIRRFV2(salarioBruto) * salarioBruto;
        double deducao = numeroDependentes * RegrasTributarias.DEDUCAO_POR_DEPENDENTE;
        double irFinal = Math.max(0.00, irBase - deducao);
        return Arredondador.duasCasas(irFinal);
    }

    private double aliquotaIRRFV2(double salarioBruto) {
        if (salarioBruto <= RegrasTributarias.LIMITE_ISENCAO_IRRF) {
            return 0.00;
        }
        if (salarioBruto < RegrasTributarias.LIMITE_PROGRESSAO_IRRF_SUPERIOR) {
            return RegrasTributarias.ALIQUOTA_IRRF;
        }
        return RegrasTributarias.ALIQUOTA_IRRF_FAIXA_SUPERIOR;
    }
}
