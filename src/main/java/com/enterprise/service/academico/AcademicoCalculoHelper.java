package com.enterprise.service.academico;

import com.enterprise.model.enums.academico.ConceitoNota;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Objects;

final class AcademicoCalculoHelper {

    private static final BigDecimal LIMITE_APROVACAO_FREQUENCIA = BigDecimal.valueOf(75);
    private static final BigDecimal LIMITE_REPROVACAO_FREQUENCIA = BigDecimal.valueOf(25);

    private AcademicoCalculoHelper() {
    }

    static Double calcularMediaNotas(Collection<ConceitoNota> conceitos) {
        double soma = conceitos.stream()
                .filter(Objects::nonNull)
                .mapToInt(ConceitoNota::getPeso)
                .sum();
        long total = conceitos.stream().filter(Objects::nonNull).count();
        if (total == 0L) {
            return null;
        }
        return soma / total;
    }

    static BigDecimal calcularMediaFrequencia(Collection<BigDecimal> frequencias) {
        BigDecimal soma = frequencias.stream()
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long total = frequencias.stream().filter(Objects::nonNull).count();
        if (total == 0L) {
            return null;
        }
        return soma.divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);
    }

    static String resolverMediaFinal(Double media) {
        ConceitoNota conceito = ConceitoNota.fromAverage(media);
        return conceito == null ? "-" : conceito.name();
    }

    static String resolverSituacaoNota(Double media) {
        if (media == null) {
            return "SEM LANCAMENTO";
        }
        return media >= ConceitoNota.R.getPeso() ? "APROVADO" : "REPROVADO";
    }

    static String resolverSituacaoFrequencia(BigDecimal media) {
        if (media == null) {
            return "SEM LANCAMENTO";
        }
        if (media.compareTo(LIMITE_APROVACAO_FREQUENCIA) >= 0) {
            return "APROVADO";
        }
        if (media.compareTo(LIMITE_REPROVACAO_FREQUENCIA) <= 0) {
            return "REPROVADO";
        }
        return "DEPENDENTE_DA_NOTA";
    }

    static String resolverSituacaoFinal(Double mediaNota, BigDecimal mediaFrequencia) {
        if (mediaNota == null && mediaFrequencia == null) {
            return "SEM LANCAMENTO";
        }
        boolean aprovadoNota = mediaNota != null && mediaNota >= ConceitoNota.R.getPeso();
        if (!aprovadoNota) {
            return "REPROVADO";
        }
        if (mediaFrequencia == null) {
            return "PENDENTE";
        }
        if (mediaFrequencia.compareTo(LIMITE_APROVACAO_FREQUENCIA) >= 0) {
            return "APROVADO";
        }
        if (mediaFrequencia.compareTo(LIMITE_REPROVACAO_FREQUENCIA) <= 0) {
            return "REPROVADO";
        }
        return "DEPENDENTE_DA_NOTA";
    }
}
