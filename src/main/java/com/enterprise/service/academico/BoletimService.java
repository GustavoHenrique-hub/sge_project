package com.enterprise.service.academico;

import com.enterprise.dto.academico.BoletimDTO;
import com.enterprise.dto.academico.BoletimDisciplinaDTO;
import com.enterprise.dto.academico.FrequenciaDTO;
import com.enterprise.dto.academico.NotaDTO;
import com.enterprise.model.entity.academico.BoletimEntity;
import com.enterprise.model.entity.gestao.AlunoEntity;
import com.enterprise.model.entity.gestao.TurmaEntity;
import com.enterprise.model.enums.academico.ConceitoNota;
import com.enterprise.repository.academico.BoletimRepository;
import com.enterprise.repository.academico.FrequenciaRepository;
import com.enterprise.repository.academico.NotaRepository;
import com.enterprise.service.gestao.AlunoService;
import com.enterprise.service.gestao.AlunoTurmaService;
import com.enterprise.service.gestao.TurmaService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
/**
 * Service responsavel pelas regras de negocio e pelos fluxos principais de BoletimService.
 */

@RequestScoped
public class BoletimService {

    @Inject
    private BoletimRepository boletimRepository;
    @Inject
    private NotaRepository notaRepository;
    @Inject
    private FrequenciaRepository frequenciaRepository;
    @Inject
    private AlunoService alunoService;
    @Inject
    private TurmaService turmaService;
    @Inject
    private AlunoTurmaService alunoTurmaService;
    /**
     * Executa uma parte da regra de negocio e organiza o fluxo principal deste servico.
     */

    public BoletimDTO obterOuCriar(Long alunoId, Long turmaId) {
        validarAlunoTurma(alunoId, turmaId);
        BoletimEntity entity = boletimRepository.findByAlunoAndTurma(alunoId, turmaId)
                .orElseGet(() -> criarBoletim(alunoId, turmaId));
        carregarColecoes(entity);
        return new BoletimDTO(entity);
    }
    /**
     * Executa uma parte da regra de negocio e organiza o fluxo principal deste servico.
     */

    public List<BoletimDisciplinaDTO> montarBoletim(Long alunoId, Long turmaId) {
        BoletimDTO boletim = obterOuCriar(alunoId, turmaId);

        Map<Long, NotaDTO> notasPorDisciplina = boletim.getNotas().stream()
                .filter(nota -> nota.getDisciplina() != null && nota.getDisciplina().getId() != null)
                .collect(Collectors.toMap(
                        n -> n.getDisciplina().getId(),
                        n -> n,
                        this::priorizarNotaMaisRecente,
                        LinkedHashMap::new
                ));

        Map<Long, FrequenciaDTO> frequenciasPorDisciplina = boletim.getFrequencias().stream()
                .filter(freq -> freq.getDisciplina() != null && freq.getDisciplina().getId() != null)
                .collect(Collectors.toMap(
                        f -> f.getDisciplina().getId(),
                        f -> f,
                        this::priorizarFrequenciaMaisRecente,
                        LinkedHashMap::new
                ));

        Set<Long> idsDisciplinas = new LinkedHashSet<>();
        idsDisciplinas.addAll(notasPorDisciplina.keySet());
        idsDisciplinas.addAll(frequenciasPorDisciplina.keySet());

        List<BoletimDisciplinaDTO> linhas = new ArrayList<>();
        for (Long disciplinaId : idsDisciplinas) {
            NotaDTO nota = notasPorDisciplina.get(disciplinaId);
            FrequenciaDTO frequencia = frequenciasPorDisciplina.get(disciplinaId);
            linhas.add(montarLinha(nota, frequencia));
        }

        linhas.sort(Comparator.comparing(item -> item.getDisciplina() == null ? "" : item.getDisciplina().getDescricao(), String.CASE_INSENSITIVE_ORDER));
        return linhas;
    }
    /**
     * Busca um unico registro pelo identificador informado, quando ele existir.
     */

    public Optional<BoletimEntity> findById(Long id) {
        return boletimRepository.findById(id).map(this::carregarColecoes);
    }

    BoletimEntity carregarColecoes(BoletimEntity entity) {
        entity.setNotas(notaRepository.findByBoletim(entity.getId()));
        entity.setFrequencias(frequenciaRepository.findByBoletim(entity.getId()));
        return entity;
    }
    /**
     * Executa uma parte da regra de negocio e organiza o fluxo principal deste servico.
     */

    private BoletimEntity criarBoletim(Long alunoId, Long turmaId) {
        AlunoEntity aluno = alunoService.findById(alunoId)
                .orElseThrow(() -> new IllegalArgumentException("Aluno nao encontrado."));
        TurmaEntity turma = turmaService.findById(turmaId)
                .orElseThrow(() -> new IllegalArgumentException("Turma nao encontrada."));

        BoletimEntity entity = new BoletimEntity();
        entity.setAluno(aluno);
        entity.setTurma(turma);
        return boletimRepository.save(entity);
    }
    /**
     * Executa uma parte da regra de negocio e organiza o fluxo principal deste servico.
     */

    private void validarAlunoTurma(Long alunoId, Long turmaId) {
        if (alunoId == null || turmaId == null) {
            throw new IllegalArgumentException("Aluno e turma sao obrigatorios.");
        }
        boolean matriculado = !alunoTurmaService.listarPorFiltros(alunoId, turmaId, null).isEmpty();
        if (!matriculado) {
            throw new IllegalArgumentException("O aluno selecionado nao esta vinculado a turma informada.");
        }
    }
    /**
     * Executa uma parte da regra de negocio e organiza o fluxo principal deste servico.
     */

    private BoletimDisciplinaDTO montarLinha(NotaDTO nota, FrequenciaDTO frequencia) {
        BoletimDisciplinaDTO linha = new BoletimDisciplinaDTO();
        linha.setDisciplina(nota != null ? nota.getDisciplina() : frequencia.getDisciplina());

        if (nota != null) {
            linha.setNota1(nota.getNota1());
            linha.setNota2(nota.getNota2());
            linha.setNota3(nota.getNota3());
            linha.setNota4(nota.getNota4());
        }
        if (frequencia != null) {
            linha.setFrequencia1(frequencia.getFrequencia1());
            linha.setFrequencia2(frequencia.getFrequencia2());
            linha.setFrequencia3(frequencia.getFrequencia3());
            linha.setFrequencia4(frequencia.getFrequencia4());
        }

        Double mediaNota = AcademicoCalculoHelper.calcularMediaNotas(Arrays.asList(
                nota == null ? null : ConceitoNota.fromValue(nota.getNota1()),
                nota == null ? null : ConceitoNota.fromValue(nota.getNota2()),
                nota == null ? null : ConceitoNota.fromValue(nota.getNota3()),
                nota == null ? null : ConceitoNota.fromValue(nota.getNota4())
        ));
        BigDecimal mediaFrequencia = AcademicoCalculoHelper.calcularMediaFrequencia(Arrays.asList(
                frequencia == null ? null : frequencia.getFrequencia1(),
                frequencia == null ? null : frequencia.getFrequencia2(),
                frequencia == null ? null : frequencia.getFrequencia3(),
                frequencia == null ? null : frequencia.getFrequencia4()
        ));

        linha.setMediaNumerica(mediaNota);
        linha.setMediaFinal(AcademicoCalculoHelper.resolverMediaFinal(mediaNota));
        linha.setSituacaoNota(AcademicoCalculoHelper.resolverSituacaoNota(mediaNota));
        linha.setPresencaFinal(mediaFrequencia);
        linha.setSituacaoFrequencia(AcademicoCalculoHelper.resolverSituacaoFrequencia(mediaFrequencia));
        linha.setSituacaoFinal(AcademicoCalculoHelper.resolverSituacaoFinal(mediaNota, mediaFrequencia));
        return linha;
    }
    /**
     * Executa uma parte da regra de negocio e organiza o fluxo principal deste servico.
     */

    private NotaDTO priorizarNotaMaisRecente(NotaDTO atual, NotaDTO nova) {
        Long idAtual = atual == null ? null : atual.getId();
        Long idNova = nova == null ? null : nova.getId();
        if (idAtual == null) {
            return nova;
        }
        if (idNova == null) {
            return atual;
        }
        return idNova > idAtual ? nova : atual;
    }
    /**
     * Executa uma parte da regra de negocio e organiza o fluxo principal deste servico.
     */

    private FrequenciaDTO priorizarFrequenciaMaisRecente(FrequenciaDTO atual, FrequenciaDTO nova) {
        Long idAtual = atual == null ? null : atual.getId();
        Long idNova = nova == null ? null : nova.getId();
        if (idAtual == null) {
            return nova;
        }
        if (idNova == null) {
            return atual;
        }
        return idNova > idAtual ? nova : atual;
    }
}
