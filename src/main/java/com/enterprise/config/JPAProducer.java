package com.enterprise.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

@ApplicationScoped
public class JPAProducer {
    private EntityManagerFactory emf;

    private EntityManagerFactory getEmf() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("meuPU");
        }
        return emf;
    }

    @Produces
    @RequestScoped
    public EntityManager em() {
        return getEmf().createEntityManager();
    }

    public void close(@Disposes EntityManager em) {
        if (em.isOpen()) em.close();
    }
}
