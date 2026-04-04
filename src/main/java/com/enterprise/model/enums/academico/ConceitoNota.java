package com.enterprise.model.enums.academico;

import java.util.Arrays;

public enum ConceitoNota {

    I("Insatisfatorio", 0),
    R("Regular", 1),
    B("Bom", 2),
    MB("Muito Bom", 3);

    private final String descricao;
    private final int peso;

    ConceitoNota(String descricao, int peso) {
        this.descricao = descricao;
        this.peso = peso;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getPeso() {
        return peso;
    }

    public static ConceitoNota fromValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return Arrays.stream(values())
                .filter(item -> item.name().equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Conceito de nota invalido: " + value));
    }

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
