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
        
        return calcularLiquidoFinal(salarioBruto, inss, irrf, 0.0);
    }

    public double calcular(double salarioBruto, int numeroDependentes, boolean optanteValeTransporte) {
        ValidadorEntrada.validarSalarioPositivo(salarioBruto);
        ValidadorEntrada.validarDependentesNaoNegativos(numeroDependentes);

        double inss = calculadoraDescontos.calcularINSS(salarioBruto);
        double irrf = obterDescontoIRRF(salarioBruto, numeroDependentes, optanteValeTransporte);
        double vt = calcularValeTransporte(salarioBruto, optanteValeTransporte);

        return calcularLiquidoFinal(salarioBruto, inss, irrf, vt);
    }

    private double obterDescontoIRRF(double salarioBruto, int numeroDependentes, boolean optanteValeTransporte) {
        // Mantém a compatibilidade com a regra de negócio anterior onde
        // sem dependentes e sem VT, usava-se o cálculo simplificado (V1)
        if (numeroDependentes == 0 && !optanteValeTransporte) {
            return calculadoraDescontos.calcularIRRF(salarioBruto);
        }
        return calculadoraDescontos.calcularIRRF(salarioBruto, numeroDependentes);
    }

    private double calcularValeTransporte(double salarioBruto, boolean optante) {
        if (!optante) {
            return 0.00;
        }
        return Arredondador.duasCasas(RegrasTributarias.ALIQUOTA_VALE_TRANSPORTE * salarioBruto);
    }

    private double calcularLiquidoFinal(double bruto, double inss, double irrf, double vt) {
        double liquido = bruto - inss - irrf - vt;
        return Arredondador.duasCasas(liquido);
    }
}
