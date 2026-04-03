package com.enterprise.dto.academico;

import com.enterprise.dto.admin.SituacaoDTO;
import com.enterprise.dto.gestao.DisciplinaDTO;
import com.enterprise.dto.gestao.AlunoDTO;
import com.enterprise.model.entity.academico.BoletimEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BoletimDTO {

    private Long id;
    private AlunoDTO Aluno;
    private DisciplinaDTO disciplina;
    private SituacaoDTO situacao;

    public BoletimDTO(BoletimEntity boletim) {
        this.id = boletim.getId();
        if (boletim.getAluno() != null) {
            this.Aluno = new AlunoDTO(boletim.getAluno());
        }
        if (boletim.getDisciplina() != null) {
            this.disciplina = new DisciplinaDTO(boletim.getDisciplina());
        }
        if (boletim.getSituacao() != null) {
            this.situacao = new SituacaoDTO(boletim.getSituacao());
        }
    }
}