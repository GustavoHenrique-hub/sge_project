package com.enterprise.repository.gestao;

import com.enterprise.config.JpaTransaction;
import com.enterprise.model.entity.gestao.ProfissionalEntity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;
/**
 * Repository que centraliza consultas e operacoes de persistencia relacionadas a ProfissionalRepository.
 */

@RequestScoped
public class ProfissionalRepository {

    @Inject
    private EntityManager em;
    /**
     * Persiste um novo registro no banco dentro do controle transacional da aplicacao.
     */

    public ProfissionalEntity save(ProfissionalEntity entity) {
        return JpaTransaction.execute(em, () -> {
            em.persist(entity);
            return entity;
        });
    }
    /**
     * Mescla e salva as alteracoes de um registro ja existente no banco de dados.
     */

    public ProfissionalEntity update(ProfissionalEntity entity) {
        return JpaTransaction.execute(em, () -> em.merge(entity));
    }
    /**
     * Busca todos os registros dessa entidade no criterio padrao adotado pela aplicacao.
     */

    public List<ProfissionalEntity> findAll() {
        return em.createQuery("select p from ProfissionalEntity p order by p.nome", ProfissionalEntity.class)
                .getResultList();
    }
    /**
     * Executa uma consulta filtrada usando os parametros recebidos pelo fluxo atual.
     */

    public List<ProfissionalEntity> findByFilters(String nome, String cpf) {
        String cpfNormalizado = cpf == null ? null : cpf.replaceAll("\\D", "");
        StringBuilder jpql = new StringBuilder("select p from ProfissionalEntity p where 1=1");
        if (nome != null && !nome.isBlank()) {
            jpql.append(" and lower(p.nome) like :nome");
        }
        if (cpfNormalizado != null && !cpfNormalizado.isBlank()) {
            jpql.append(" and p.cpf like :cpf");
        }
        jpql.append(" order by p.nome");

        TypedQuery<ProfissionalEntity> query = em.createQuery(jpql.toString(), ProfissionalEntity.class);
        if (nome != null && !nome.isBlank()) {
            query.setParameter("nome", "%" + nome.trim().toLowerCase() + "%");
        }
        if (cpfNormalizado != null && !cpfNormalizado.isBlank()) {
            query.setParameter("cpf", "%" + cpfNormalizado.trim() + "%");
        }
        return query.getResultList();
    }
    /**
     * Executa uma operacao de persistencia ou consulta usada pelo restante do modulo.
     */

    public List<ProfissionalEntity> findProfessoresAtivosByTermo(String termo) {
        String termoNormalizado = termo == null ? "" : termo.trim().toLowerCase();
        String termoNumerico = termo == null ? "" : termo.replaceAll("\\D", "");

        StringBuilder jpql = new StringBuilder(
                "select distinct p from ProfissionalEntity p " +
                        "where exists (" +
                        "select 1 from com.enterprise.model.entity.admin.PerfilUsuarioEntity au " +
                        "join au.usuario u " +
                        "join au.perfil pe " +
                        "join au.situacao s " +
                        "where u.profissional.id = p.id " +
                        "and u.profissional.rm = p.rm " +
                        "and upper(s.situacao) = :situacao " +
                        "and upper(pe.perfil) = :perfil" +
                        ")"
        );

        boolean filtrarTexto = !termoNormalizado.isBlank();
        boolean filtrarNumero = !termoNumerico.isBlank();

        if (filtrarTexto || filtrarNumero) {
            jpql.append(" and (");
            boolean precisaOr = false;

            if (filtrarTexto) {
                jpql.append("lower(p.nome) like :termoTexto or lower(p.rm) like :termoTexto");
                precisaOr = true;
            }
            if (filtrarNumero) {
                if (precisaOr) {
                    jpql.append(" or ");
                }
                jpql.append("p.cpf like :termoNumero");
            }
            jpql.append(")");
        }

        jpql.append(" order by p.nome");

        TypedQuery<ProfissionalEntity> query = em.createQuery(jpql.toString(), ProfissionalEntity.class)
                .setParameter("situacao", "ATIVO")
                .setParameter("perfil", "PROFESSOR");

        if (filtrarTexto) {
            query.setParameter("termoTexto", "%" + termoNormalizado + "%");
        }
        if (filtrarNumero) {
            query.setParameter("termoNumero", "%" + termoNumerico + "%");
        }

        return query.getResultList();
    }
    /**
     * Busca um unico registro pelo identificador informado, quando ele existir.
     */

    public Optional<ProfissionalEntity> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return em.createQuery(
                        "select p from ProfissionalEntity p where p.id = :id",
                        ProfissionalEntity.class
                )
                .setParameter("id", id)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }
}

