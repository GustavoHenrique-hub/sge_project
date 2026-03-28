package com.enterprise.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.function.Supplier;

public final class JpaTransaction {

    private JpaTransaction() {
    }

    public static <T> T execute(EntityManager em, Supplier<T> action) {
        EntityTransaction transaction = em.getTransaction();
        boolean owner = !transaction.isActive();
        if (owner) {
            transaction.begin();
        }
        try {
            T result = action.get();
            if (owner) {
                transaction.commit();
            }
            return result;
        } catch (RuntimeException ex) {
            if (owner && transaction.isActive()) {
                transaction.rollback();
            }
            throw ex;
        }
    }

    public static void run(EntityManager em, Runnable action) {
        execute(em, () -> {
            action.run();
            return null;
        });
    }
}
