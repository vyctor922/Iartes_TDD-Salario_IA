package br.com.iartes.salario;

import br.com.iartes.salario.util.Arredondador;
import br.com.iartes.salario.validacao.ValidadorEntrada;

public class CalculadoraDescontos {

    public double calcularINSS(double salarioBruto) {
        ValidadorEntrada.validarSalarioPositivo(salarioBruto);
        double descontoBase = RegrasTributarias.ALIQUOTA_INSS * salarioBruto;
        // Utiliza Math.min para garantir o respeito ao teto de forma concisa
        double descontoFinal = Math.min(descontoBase, RegrasTributarias.TETO_INSS);
        return Arredondador.duasCasas(descontoFinal);
    }

    /**
     * Cálculo simplificado (V1) - Regra de 10% acima da isenção.
     */
    public double calcularIRRF(double salarioBruto) {
        ValidadorEntrada.validarSalarioPositivo(salarioBruto);
        if (salarioBruto <= RegrasTributarias.LIMITE_ISENCAO_IRRF) {
            return 0.00;
        }
        return Arredondador.duasCasas(RegrasTributarias.ALIQUOTA_IRRF * salarioBruto);
    }

    /**
     * Cálculo progressivo (V2) - Com dependentes e faixas.
     */
    public double calcularIRRF(double salarioBruto, int numeroDependentes) {
        ValidadorEntrada.validarSalarioPositivo(salarioBruto);
        ValidadorEntrada.validarDependentesNaoNegativos(numeroDependentes);

        double irBase = obterAliquotaProgressiva(salarioBruto) * salarioBruto;
        double deducaoDependentes = numeroDependentes * RegrasTributarias.DEDUCAO_POR_DEPENDENTE;
        
        // Garante que o imposto não seja negativo
        double irFinal = Math.max(0.00, irBase - deducaoDependentes);
        
        return Arredondador.duasCasas(irFinal);
    }

    private double obterAliquotaProgressiva(double salarioBruto) {
        if (salarioBruto <= RegrasTributarias.LIMITE_ISENCAO_IRRF) {
            return 0.00;
        }
        if (salarioBruto < RegrasTributarias.LIMITE_PROGRESSAO_IRRF_SUPERIOR) {
            return RegrasTributarias.ALIQUOTA_IRRF;
        }
        return RegrasTributarias.ALIQUOTA_IRRF_FAIXA_SUPERIOR;
    }
}
