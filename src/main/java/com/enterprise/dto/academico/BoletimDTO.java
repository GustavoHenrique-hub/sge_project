package com.enterprise.dto.academico;

import com.enterprise.dto.gestao.AlunoDTO;
import com.enterprise.dto.gestao.TurmaDTO;
import com.enterprise.model.entity.academico.FrequenciaEntity;
import com.enterprise.dto.gestao.DisciplinaDTO;
import com.enterprise.model.entity.academico.BoletimEntity;
import com.enterprise.model.entity.academico.NotaEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BoletimDTO {

    private Long id;
    private AlunoDTO aluno;
    private TurmaDTO turma;
    private List<NotaDTO> notas = new ArrayList<>();
    private List<FrequenciaDTO> frequencias = new ArrayList<>();

    public BoletimDTO(BoletimEntity boletim) {
        this.id = boletim.getId();
        if (boletim.getAluno() != null) {
            this.aluno = new AlunoDTO(boletim.getAluno());
        }
        if (boletim.getTurma() != null) {
            this.turma = new TurmaDTO(boletim.getTurma());
        }
        if (boletim.getNotas() != null) {
            for (NotaEntity nota : boletim.getNotas()) {
                this.notas.add(new NotaDTO(nota));
            }
        }
        if (boletim.getFrequencias() != null) {
            for (FrequenciaEntity frequencia : boletim.getFrequencias()) {
                this.frequencias.add(new FrequenciaDTO(frequencia));
            }
        }
    }
}
