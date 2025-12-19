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
 *   - `double calcular(double salarioBruto)`
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
            metodoCalcular = clazz.getDeclaredMethod("calcular", double.class);
        } catch (NoSuchMethodException e) {
            fail("Método esperado não encontrado: calcular(double). " +
                    "Implemente a fórmula: líquido = bruto − INSS − IR, com arredondamento final.");
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

    private double esperadoLiquido(double salario) {
        return round2(salario - inss(salario) - irrf(salario));
    }

    private double invokeCalcular(double salario) {
        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();
            Object result = metodoCalcular.invoke(instance, salario);
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
        @DisplayName("deve_calcular_salario_liquido_com_regras_basicas")
        void Deve_calcular_salario_liquido_com_regras_basicas() {
            double s1 = 1000.00;
            double obtido1 = round2(invokeCalcular(s1));
            double esperado1 = esperadoLiquido(s1);
            assertEquals(esperado1, obtido1,
                    "Salário líquido para " + s1 + " deve ser " + esperado1 + " mas foi " + obtido1);

            double s2 = 6250.00; // ponto de teto do INSS
            double obtido2 = round2(invokeCalcular(s2));
            double esperado2 = esperadoLiquido(s2);
            assertEquals(esperado2, obtido2,
                    "Salário líquido para " + s2 + " deve ser " + esperado2 + " mas foi " + obtido2);

            double s3 = 10000.00; // INSS teto e IRRF 10%
            double obtido3 = round2(invokeCalcular(s3));
            double esperado3 = esperadoLiquido(s3);
            assertEquals(esperado3, obtido3,
                    "Salário líquido para " + s3 + " deve ser " + esperado3 + " mas foi " + obtido3);

            double s4 = 2000.00; // isento IRRF
            double obtido4 = round2(invokeCalcular(s4));
            double esperado4 = esperadoLiquido(s4);
            assertEquals(esperado4, obtido4,
                    "Salário líquido para " + s4 + " deve ser " + esperado4 + " mas foi " + obtido4);

            double s5 = 2000.01; // IRRF 10% aplicável
            double obtido5 = round2(invokeCalcular(s5));
            double esperado5 = esperadoLiquido(s5);
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
                assertThrows(IllegalArgumentException.class,
                        () -> metodoCalcular.invoke(instance, 0.00),
                        "Deve lançar IllegalArgumentException para salário igual a zero");
                assertThrows(IllegalArgumentException.class,
                        () -> metodoCalcular.invoke(instance, -1.00),
                        "Deve lançar IllegalArgumentException para salário negativo");
            } catch (ReflectiveOperationException e) {
                fail("Falha de reflexão ao preparar instância: " + e.getMessage());
            }
        }
    }
}

