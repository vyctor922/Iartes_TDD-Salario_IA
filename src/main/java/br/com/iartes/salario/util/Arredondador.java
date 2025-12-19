package br.com.iartes.salario.util;

public final class Arredondador {
    private Arredondador() {}

    public static double duasCasas(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}

