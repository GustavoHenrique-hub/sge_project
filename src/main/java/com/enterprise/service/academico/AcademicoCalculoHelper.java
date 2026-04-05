package com.enterprise.service.academico;

import com.enterprise.model.enums.academico.ConceitoNota;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Objects;

/**
 * Classe utilitaria que concentra os calculos e a interpretacao de status do modulo academico.
 */

final class AcademicoCalculoHelper {

    private static final BigDecimal LIMITE_APROVACAO_FREQUENCIA = BigDecimal.valueOf(75);
    private static final BigDecimal LIMITE_REPROVACAO_FREQUENCIA = BigDecimal.valueOf(25);
    private AcademicoCalculoHelper() {
    }

    /**
     * Calcula a media simples dos conceitos lancados ignorando valores nulos.
     */
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
    /**
     * Calcula a media percentual de frequencia usando duas casas decimais para padronizar a exibicao.
     */
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
    /**
     * Converte a media numerica no conceito final exibido para a tela.
     */
    static String resolverMediaFinal(Double media) {
        ConceitoNota conceito = ConceitoNota.fromAverage(media);
        return conceito == null ? "-" : conceito.name();
    }
    /**
     * Define a situacao pela nota considerando aprovacao quando a media atinge o peso minimo do conceito R.
     */
    static String resolverSituacaoNota(Double media) {
        if (media == null) {
            return "SEM LANCAMENTO";
        }
        return media >= ConceitoNota.R.getPeso() ? "APROVADO" : "REPROVADO";
    }
    /**
     * Define a situacao pela frequencia usando 75% como aprovacao e 25% como reprovacao direta.
     */
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
    /**
     * Combina nota e frequencia para devolver a situacao final consolidada do aluno.
     */
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
