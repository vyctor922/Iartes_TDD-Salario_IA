package br.com.iartes.salario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suíte de testes para descontos obrigatórios (INSS e IRRF).
 * Regras (contexto):
 * - INSS: 8% sobre o salário bruto com teto de R$ 500,00.
 * - IRRF: Isento até R$ 2.000,00; 10% sobre o salário bruto para salários acima de R$ 2.000,00.
 * - Entradas <= 0 geram erro/exceção.
 * - Resultado deve ser arredondado para duas casas decimais (aplicado nas assertivas).
 *
 * Observação: Os testes usam reflexão para não impor assinatura específica.
 * Espera-se existir uma classe `br.com.iartes.salario.CalculadoraDescontos`
 * com métodos:
 *   - `double calcularINSS(double salarioBruto)`
 *   - `double calcularIRRF(double salarioBruto)`
 */
class CalculadoraDescontosTest {

    private Class<?> clazz;
    private Method metodoINSS;
    private Method metodoIRRF;

    /**
     * Configuração comum: resolve a classe e métodos esperados via reflexão.
     * Falha com mensagem detalhada caso a implementação ainda não exista.
     */
    @BeforeEach
    void setup() {
        try {
            clazz = Class.forName("br.com.iartes.salario.CalculadoraDescontos");
        } catch (ClassNotFoundException e) {
            fail("Classe esperada não encontrada: br.com.iartes.salario.CalculadoraDescontos. " +
                    "Crie a classe com os métodos calcularINSS(double) e calcularIRRF(double).");
        }
        try {
            metodoINSS = clazz.getDeclaredMethod("calcularINSS", double.class);
        } catch (NoSuchMethodException e) {
            fail("Método esperado não encontrado: calcularINSS(double). " +
                    "Implemente o cálculo de INSS: 8% com teto de R$ 500,00.");
        }
        try {
            metodoIRRF = clazz.getDeclaredMethod("calcularIRRF", double.class);
        } catch (NoSuchMethodException e) {
            fail("Método esperado não encontrado: calcularIRRF(double). " +
                    "Implemente a regra: isento até R$ 2.000,00; 10% acima disso.");
        }
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private double invokeDouble(Method m, double salario) {
        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();
            Object result = m.invoke(instance, salario);
            assertNotNull(result, "Resultado não deve ser nulo para salário " + salario);
            assertTrue(result instanceof Double,
                    "O tipo de retorno deve ser double. Obtido: " + result.getClass().getName());
            return (double) result;
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            fail("Invocação do método lançou exceção: " +
                    (cause != null ? cause.getClass().getName() + " - " + cause.getMessage() : e.getMessage()));
            return Double.NaN;
        } catch (ReflectiveOperationException e) {
            fail("Falha de reflexão ao invocar método: " + e.getMessage());
            return Double.NaN;
        }
    }

    @Nested
    class INSS {

        /**
         * Verifica alíquota de 8% para salário baixo e teto de R$ 500,00 para salário alto.
         */
        @Test
        @DisplayName("deve_aplicar_aliquota_INSS_corretamente_por_faixa_salarial")
        void Deve_aplicar_aliquota_INSS_corretamente_por_faixa_salarial() {
            double salarioBaixo = 1000.00;
            double esperadoBaixo = round2(0.08 * salarioBaixo); // 80.00
            double obtidoBaixo = round2(invokeDouble(metodoINSS, salarioBaixo));
            assertEquals(esperadoBaixo, obtidoBaixo,
                    "INSS 8% para salário " + salarioBaixo + " deve ser " + esperadoBaixo + " mas foi " + obtidoBaixo);

            double salarioAlto = 10000.00;
            double esperadoAlto = 500.00; // teto
            double obtidoAlto = round2(invokeDouble(metodoINSS, salarioAlto));
            assertEquals(esperadoAlto, obtidoAlto,
                    "INSS teto para salário " + salarioAlto + " deve ser " + esperadoAlto + " mas foi " + obtidoAlto);

            double salarioTeto = 6250.00; // 8% => 500.00 exato
            double esperadoTeto = 500.00;
            double obtidoTeto = round2(invokeDouble(metodoINSS, salarioTeto));
            assertEquals(esperadoTeto, obtidoTeto,
                    "INSS no ponto de teto para salário " + salarioTeto + " deve ser " + esperadoTeto + " mas foi " + obtidoTeto);
        }
    }

    @Nested
    class IRRF {

        /**
         * Verifica isenção até 2000.00 e tributação de 10% acima de 2000.00.
         */
        @Test
        @DisplayName("deve_calcular_IRRF_considerando_dependentes")
        void Deve_calcular_IRRF_considerando_dependentes() {
            // Domínio não define dependentes; validamos regra básica do contexto.
            double salarioIsento = 2000.00;
            double esperadoIsento = 0.00;
            double obtidoIsento = round2(invokeDouble(metodoIRRF, salarioIsento));
            assertEquals(esperadoIsento, obtidoIsento,
                    "IRRF para salário " + salarioIsento + " deve ser isento (" + esperadoIsento + ") mas foi " + obtidoIsento);

            double salarioTributavel = 2000.01;
            double esperadoTributavel = round2(0.10 * salarioTributavel);
            double obtidoTributavel = round2(invokeDouble(metodoIRRF, salarioTributavel));
            assertEquals(esperadoTributavel, obtidoTributavel,
                    "IRRF 10% para salário " + salarioTributavel + " deve ser " + esperadoTributavel + " mas foi " + obtidoTributavel);
        }
    }

    @Nested
    class Excecoes {

        /**
         * Entradas negativas devem lançar exceção (IllegalArgumentException).
         */
        @Test
        @DisplayName("deve_lancar_excecao_para_valores_negativos")
        void Deve_lancar_excecao_para_valores_negativos() {
            double salarioNegativo = -100.00;
            try {
                Object instance = clazz.getDeclaredConstructor().newInstance();
                assertThrows(IllegalArgumentException.class, () -> {
                            try {
                                metodoINSS.invoke(instance, salarioNegativo);
                            } catch (InvocationTargetException e) {
                                Throwable cause = e.getCause();
                                if (cause instanceof RuntimeException re) throw re;
                                throw new RuntimeException(cause);
                            }
                        },
                        "Deve lançar IllegalArgumentException para INSS com salário negativo");
                assertThrows(IllegalArgumentException.class, () -> {
                            try {
                                metodoIRRF.invoke(instance, salarioNegativo);
                            } catch (InvocationTargetException e) {
                                Throwable cause = e.getCause();
                                if (cause instanceof RuntimeException re) throw re;
                                throw new RuntimeException(cause);
                            }
                        },
                        "Deve lançar IllegalArgumentException para IRRF com salário negativo");
            } catch (ReflectiveOperationException e) {
                fail("Falha de reflexão ao preparar instância: " + e.getMessage());
            }
        }
    }
    @Nested
    class Arredondamento {
        @Test
        @DisplayName("deve_arredondar_resultados_para_duas_casas")
        void Deve_arredondar_resultados_para_duas_casas() {
            double salario = 1234.567;
            double esperadoINSS = round2(Math.min(0.08 * salario, 500.00));
            double obtidoINSS = round2(invokeDouble(metodoINSS, salario));
            assertEquals(esperadoINSS, obtidoINSS,
                    "INSS deve ser arredondado para duas casas. Esperado " + esperadoINSS + " obtido " + obtidoINSS);

            double salarioAlto = 9876.543;
            double esperadoIR = round2(0.10 * salarioAlto);
            double obtidoIR = round2(invokeDouble(metodoIRRF, salarioAlto));
            assertEquals(esperadoIR, obtidoIR,
                    "IRRF deve ser arredondado para duas casas. Esperado " + esperadoIR + " obtido " + obtidoIR);
        }
    }
}
