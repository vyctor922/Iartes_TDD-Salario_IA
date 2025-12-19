package br.com.iartes.salario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suíte de testes para cálculo de salário líquido.
 * Regras (contexto):
 * - salário líquido = salário bruto − desconto INSS − desconto IR
 * - INSS: 8% com teto de R$ 500,00
 * - IRRF: isento até R$ 2.000,00; 10% acima disso
 * - salário <= 0 deve lançar exceção
 * - resultado arredondado para duas casas decimais
 *
 * Observação: Usa reflexão para não impor assinatura fixa.
     * Espera-se a classe `br.com.iartes.salario.CalculadoraSalarioLiquido` com:
     *   - `double calcular(double salarioBruto, int numeroDependentes, boolean optanteValeTransporte)`
 */
class CalculadoraSalarioLiquidoTest {

    private Class<?> clazz;
    private Method metodoCalcular;

    /**
     * Configuração comum: resolve a classe e método esperado via reflexão.
     * Falha com mensagem detalhada caso a implementação ainda não exista.
     */
    @BeforeEach
    void setup() {
        try {
            clazz = Class.forName("br.com.iartes.salario.CalculadoraSalarioLiquido");
        } catch (ClassNotFoundException e) {
            fail("Classe esperada não encontrada: br.com.iartes.salario.CalculadoraSalarioLiquido. " +
                    "Crie a classe com o método calcular(double).");
        }
        try {
            metodoCalcular = clazz.getDeclaredMethod("calcular", double.class, int.class, boolean.class);
        } catch (NoSuchMethodException e) {
            fail("Método esperado não encontrado: calcular(double, int, boolean). " +
                    "V2 deve considerar dependentes e vale-transporte.");
        }
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private double inss(double salario) {
        return round2(Math.min(0.08 * salario, 500.00));
    }

    private double irrf(double salario) {
        return round2(salario > 2000.00 ? 0.10 * salario : 0.00);
    }

    private double vt(double salario, boolean optante) {
        return optante ? round2(0.06 * salario) : 0.00;
    }

    private double esperadoLiquidoV1(double salario, int dependentes, boolean optanteVT) {
        // Retrocompatibilidade V1: dependentes=0, VT=false não alteram resultado
        return round2(salario - inss(salario) - irrf(salario) - vt(salario, optanteVT));
    }

    private double invokeCalcular(double salario, int dependentes, boolean optanteVT) {
        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();
            Object result = metodoCalcular.invoke(instance, salario, dependentes, optanteVT);
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
    class Positivos {

        /**
         * Verifica cálculo do salário líquido em cenários típicos e limites.
         */
        @Test
        @DisplayName("deve_calcular_salario_liquido_com_regras_basicas_V1_com_dependentes_0_e_VT_false")
        void Deve_calcular_salario_liquido_com_regras_basicas_V1_com_dependentes_0_e_VT_false() {
            double s1 = 1000.00;
            double obtido1 = round2(invokeCalcular(s1, 0, false));
            double esperado1 = esperadoLiquidoV1(s1, 0, false);
            assertEquals(esperado1, obtido1,
                    "Salário líquido para " + s1 + " deve ser " + esperado1 + " mas foi " + obtido1);

            double s2 = 6250.00; // ponto de teto do INSS
            double obtido2 = round2(invokeCalcular(s2, 0, false));
            double esperado2 = esperadoLiquidoV1(s2, 0, false);
            assertEquals(esperado2, obtido2,
                    "Salário líquido para " + s2 + " deve ser " + esperado2 + " mas foi " + obtido2);

            double s3 = 10000.00; // INSS teto e IRRF 10%
            double obtido3 = round2(invokeCalcular(s3, 0, false));
            double esperado3 = esperadoLiquidoV1(s3, 0, false);
            assertEquals(esperado3, obtido3,
                    "Salário líquido para " + s3 + " deve ser " + esperado3 + " mas foi " + obtido3);

            double s4 = 2000.00; // isento IRRF
            double obtido4 = round2(invokeCalcular(s4, 0, false));
            double esperado4 = esperadoLiquidoV1(s4, 0, false);
            assertEquals(esperado4, obtido4,
                    "Salário líquido para " + s4 + " deve ser " + esperado4 + " mas foi " + obtido4);

            double s5 = 2000.01; // IRRF 10% aplicável
            double obtido5 = round2(invokeCalcular(s5, 0, false));
            double esperado5 = esperadoLiquidoV1(s5, 0, false);
            assertEquals(esperado5, obtido5,
                    "Salário líquido para " + s5 + " deve ser " + esperado5 + " mas foi " + obtido5);
        }
    }

    @Nested
    class Excecoes {

        /**
         * Salário igual ou inferior a zero deve lançar IllegalArgumentException.
         */
        @Test
        @DisplayName("deve_lancar_excecao_para_valores_invalidos")
        void Deve_lancar_excecao_para_valores_invalidos() {
            try {
                Object instance = clazz.getDeclaredConstructor().newInstance();
                assertThrows(IllegalArgumentException.class, () -> {
                            try {
                                metodoCalcular.invoke(instance, 0.00, 0, false);
                            } catch (InvocationTargetException e) {
                                Throwable cause = e.getCause();
                                if (cause instanceof RuntimeException re) throw re;
                                throw new RuntimeException(cause);
                            }
                        },
                        "Deve lançar IllegalArgumentException para salário igual a zero");
                assertThrows(IllegalArgumentException.class, () -> {
                            try {
                                metodoCalcular.invoke(instance, -1.00, 0, false);
                            } catch (InvocationTargetException e) {
                                Throwable cause = e.getCause();
                                if (cause instanceof RuntimeException re) throw re;
                                throw new RuntimeException(cause);
                            }
                        },
                        "Deve lançar IllegalArgumentException para salário negativo");
            } catch (ReflectiveOperationException e) {
                fail("Falha de reflexão ao preparar instância: " + e.getMessage());
            }
        }

        @Test
        @DisplayName("deve_lancar_excecao_para_dependentes_negativos")
        void Deve_lancar_excecao_para_dependentes_negativos() {
            try {
                Object instance = clazz.getDeclaredConstructor().newInstance();
                assertThrows(IllegalArgumentException.class, () -> {
                            try {
                                metodoCalcular.invoke(instance, 3000.00, -1, false);
                            } catch (InvocationTargetException e) {
                                Throwable cause = e.getCause();
                                if (cause instanceof RuntimeException re) throw re;
                                throw new RuntimeException(cause);
                            }
                        },
                        "Deve lançar IllegalArgumentException para número de dependentes negativo");
            } catch (ReflectiveOperationException e) {
                fail("Falha de reflexão ao preparar instância: " + e.getMessage());
            }
        }
    }

    @Nested
    class Arredondamento {

        @Test
        @DisplayName("deve_arredondar_liquido_para_duas_casas")
        void Deve_arredondar_liquido_para_duas_casas() {
            double salario = 1234.567;
            double esperado = round2(salario - inss(salario) - irrf(salario));
            double obtido = round2(invokeCalcular(salario, 0, false));
            assertEquals(esperado, obtido,
                    "Salário líquido deve ser arredondado para duas casas. Esperado " + esperado + " obtido " + obtido);
        }
    }

    @Nested
    class ValeTransporteV2 {
        @Test
        @DisplayName("deve_aplicar_desconto_de_6_porcento_quando_optante_VT_true")
        void Deve_aplicar_desconto_de_6_porcento_quando_optante_VT_true() {
            double bruto = 3000.00;
            int dependentes = 0;
            boolean vt = true;
            double esperado = esperadoLiquidoV1(bruto, dependentes, vt);
            double obtido = round2(invokeCalcular(bruto, dependentes, vt));
            assertEquals(esperado, obtido,
                    "Quando optante pelo vale-transporte, deve aplicar desconto de 6% sobre o bruto.");
        }

        @Test
        @DisplayName("deve_aplicar_desconto_zero_quando_optante_VT_false")
        void Deve_aplicar_desconto_zero_quando_optante_VT_false() {
            double bruto = 3000.00;
            int dependentes = 0;
            boolean vt = false;
            double esperado = esperadoLiquidoV1(bruto, dependentes, vt);
            double obtido = round2(invokeCalcular(bruto, dependentes, vt));
            assertEquals(esperado, obtido,
                    "Quando não optante pelo vale-transporte, desconto deve ser zero.");
        }
    }
}
