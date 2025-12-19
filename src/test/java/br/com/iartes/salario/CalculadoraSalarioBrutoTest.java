package br.com.iartes.salario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suíte de testes para a funcionalidade de cálculo de salário bruto.
 * Baseada estritamente no contexto: o sistema recebe o salário bruto como entrada
 * (valor numérico positivo). Como o domínio não define horas trabalhadas ou horas extras,
 * os testes relacionados a esses cenários são documentados e desabilitados.
 */
class CalculadoraSalarioBrutoTest {

    private double salarioBase;

    /**
     * Configura dados comuns e garante independência entre casos.
     */
    @BeforeEach
    void setup() {
        salarioBase = 2000.00;
    }

    @Nested
    class Basicos {

        /**
         * Caso de teste documentado conforme requisito, porém fora do escopo do domínio atual.
         * O domínio não especifica cálculo de salário bruto a partir de horas trabalhadas,
         * apenas recebe o salário bruto diretamente como entrada.
         */
        @Test
        @Disabled("Fora do escopo: domínio não define cálculo por horas trabalhadas")
        @DisplayName("deve_calcular_salario_bruto_corretamente_para_horas_trabalhadas")
        void Deve_calcular_salario_bruto_corretamente_para_horas_trabalhadas() {
            fail("Este caso é um placeholder documentado. O domínio não inclui horas trabalhadas.");
        }

        /**
         * Caso de teste documentado conforme requisito, porém fora do escopo do domínio atual.
         * O domínio não especifica horas extras; a entrada já é o salário bruto.
         */
        @Test
        @Disabled("Fora do escopo: domínio não define horas extras")
        @DisplayName("deve_calcular_salario_bruto_com_horas_extras")
        void Deve_calcular_salario_bruto_com_horas_extras() {
            fail("Este caso é um placeholder documentado. O domínio não inclui horas extras.");
        }
    }
}

