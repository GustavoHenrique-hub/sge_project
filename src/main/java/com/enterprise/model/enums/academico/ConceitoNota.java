package com.enterprise.model.enums.academico;

import java.util.Arrays;
/**
 * Enum que organiza valores padrao usados nas regras de negocio desta area.
 */

public enum ConceitoNota {

    I("Insatisfatorio", 0),
    R("Regular", 1),
    B("Bom", 2),
    MB("Muito Bom", 3);

    private final String descricao;
    private final int peso;
    /**
     * Executa a responsabilidade principal deste metodo dentro da classe.
     */

    ConceitoNota(String descricao, int peso) {
        this.descricao = descricao;
        this.peso = peso;
    }
    /**
     * Executa a responsabilidade principal deste metodo dentro da classe.
     */

    public String getDescricao() {
        return descricao;
    }
    /**
     * Executa a responsabilidade principal deste metodo dentro da classe.
     */

    public int getPeso() {
        return peso;
    }
    /**
     * Executa a responsabilidade principal deste metodo dentro da classe.
     */

    public static ConceitoNota fromValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return Arrays.stream(values())
                .filter(item -> item.name().equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Conceito de nota invalido: " + value));
    }
    /**
     * Executa a responsabilidade principal deste metodo dentro da classe.
     */

    public static ConceitoNota fromAverage(Double media) {
        if (media == null) {
            return null;
        }
        if (media < 0.5d) {
            return I;
        }
        if (media < 1.5d) {
            return R;
        }
        if (media < 2.5d) {
            return B;
        }
        return MB;
    }
}
