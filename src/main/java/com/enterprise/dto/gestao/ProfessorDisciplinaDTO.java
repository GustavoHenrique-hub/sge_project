package com.enterprise.dto.gestao;

import com.enterprise.dto.admin.SituacaoDTO;
import com.enterprise.model.entity.gestao.AlunoTurmaEntity;
import com.enterprise.model.entity.gestao.ProfessorDisciplinaEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProfessorDisciplinaDTO {

    private Long id;
    private ProfessorDTO professor;
    private DisciplinaDTO disciplina;
    private SituacaoDTO situacao;

    public ProfessorDisciplinaDTO(ProfessorDisciplinaEntity professorDisciplina) {
        this.id = professorDisciplina.getId();
        if (professorDisciplina.getProfessor() != null) {
            this.professor = new ProfessorDTO(professorDisciplina.getProfessor());
        }
        if (professorDisciplina.getDisciplina() != null) {
            this.disciplina = new DisciplinaDTO(professorDisciplina.getDisciplina());
        }
        if (professorDisciplina.getSituacao() != null) {
            this.situacao = new SituacaoDTO(professorDisciplina.getSituacao());
        }
    }
}