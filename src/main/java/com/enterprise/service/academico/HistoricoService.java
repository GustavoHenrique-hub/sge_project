package com.enterprise.service.academico;

import com.enterprise.dto.academico.BoletimDisciplinaDTO;
import com.enterprise.dto.academico.HistoricoDTO;
import com.enterprise.dto.academico.HistoricoDisciplinaDTO;
import com.enterprise.model.entity.academico.BoletimEntity;
import com.enterprise.model.entity.academico.HistoricoEntity;
import com.enterprise.repository.academico.HistoricoRepository;
import com.enterprise.service.gestao.AlunoService;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
/**
 * Service responsavel pelas regras de negocio e pelos fluxos principais de HistoricoService.
 */

@RequestScoped
public class HistoricoService {

    @Inject
    private HistoricoRepository historicoRepository;
    @Inject
    private BoletimService boletimService;
    @Inject
    private AlunoService alunoService;
    /**
     * Executa uma parte da regra de negocio e organiza o fluxo principal deste servico.
     */

    public HistoricoDTO gerarHistorico(Long alunoId) {
        if (alunoId == null) {
            throw new IllegalArgumentException("Aluno e obrigatorio.");
        }

        HistoricoEntity historico = new HistoricoEntity();
        historico.setAluno(alunoService.findById(alunoId)
                .map(com.enterprise.dto.gestao.AlunoDTO::new)
                .orElseThrow(() -> new IllegalArgumentException("Aluno nao encontrado.")));

        List<HistoricoDisciplinaDTO> disciplinas = new ArrayList<>();
        for (BoletimEntity boletim : historicoRepository.findBoletinsByAluno(alunoId)) {
            for (BoletimDisciplinaDTO linha : boletimService.montarBoletim(boletim.getAluno().getId(), boletim.getTurma().getId())) {
                HistoricoDisciplinaDTO item = new HistoricoDisciplinaDTO();
                item.setTurma(new com.enterprise.dto.gestao.TurmaDTO(boletim.getTurma()));
                item.setDisciplina(linha.getDisciplina());
                item.setMediaFinal(linha.getMediaFinal());
                item.setSituacaoNota(linha.getSituacaoNota());
                item.setPresencaFinal(linha.getPresencaFinal());
                item.setSituacaoFrequencia(linha.getSituacaoFrequencia());
                item.setSituacaoFinal(linha.getSituacaoFinal());
                disciplinas.add(item);
            }
        }

        historico.setDisciplinas(disciplinas);
        return new HistoricoDTO(historico);
    }
    /**
     * Executa uma parte da regra de negocio e organiza o fluxo principal deste servico.
     */

    public byte[] gerarPdf(Long alunoId) {
        HistoricoDTO historico = gerarHistorico(alunoId);
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, output);
            document.open();

            Font titulo = new Font(Font.HELVETICA, 16, Font.BOLD);
            Font normal = new Font(Font.HELVETICA, 11, Font.NORMAL);

            document.add(new Paragraph("Historico Escolar", titulo));
            document.add(new Paragraph("Aluno: " + historico.getAluno().getNome(), normal));
            document.add(new Paragraph("RM: " + historico.getAluno().getRm(), normal));
            document.add(new Paragraph("CPF: " + historico.getAluno().getCpf(), normal));
            document.add(new Paragraph("RG: " + historico.getAluno().getRg(), normal));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2.2f, 2.5f, 1.2f, 1.4f, 1.8f, 1.8f});

            adicionarCabecalho(table, "Turma");
            adicionarCabecalho(table, "Disciplina");
            adicionarCabecalho(table, "Media");
            adicionarCabecalho(table, "Presenca");
            adicionarCabecalho(table, "Sit. Nota");
            adicionarCabecalho(table, "Sit. Final");

            for (HistoricoDisciplinaDTO item : historico.getDisciplinas()) {
                table.addCell(valor(item.getTurma() == null ? null : item.getTurma().getTurma()));
                table.addCell(valor(item.getDisciplina() == null ? null : item.getDisciplina().getDescricao()));
                table.addCell(valor(item.getMediaFinal()));
                table.addCell(valor(item.getPresencaFinal() == null ? null : item.getPresencaFinal() + "%"));
                table.addCell(valor(item.getSituacaoNota()));
                table.addCell(valor(item.getSituacaoFinal()));
            }

            document.add(table);
            document.close();
            return output.toByteArray();
        } catch (DocumentException ex) {
            throw new IllegalStateException("Falha ao gerar PDF do historico.", ex);
        } catch (Exception ex) {
            throw new IllegalStateException("Falha ao montar o historico em PDF.", ex);
        }
    }
    /**
     * Executa uma parte da regra de negocio e organiza o fluxo principal deste servico.
     */

    private void adicionarCabecalho(PdfPTable table, String valor) {
        PdfPCell cell = new PdfPCell(new Phrase(valor));
        cell.setPadding(6f);
        table.addCell(cell);
    }
    /**
     * Executa uma parte da regra de negocio e organiza o fluxo principal deste servico.
     */

    private String valor(String valor) {
        return valor == null || valor.isBlank() ? "-" : valor;
    }
}
