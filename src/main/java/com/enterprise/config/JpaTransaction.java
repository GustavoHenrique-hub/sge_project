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
                try {
                    transaction.commit();
                } catch (RuntimeException ex) {
                    if (transaction.isActive()) {
                        transaction.rollback();
                    }
                    throw enrich(ex);
                }
            }
            return result;
        } catch (RuntimeException ex) {
            if (owner && transaction.isActive()) {
                transaction.rollback();
            }
            throw enrich(ex);
        }
    }

    public static void run(EntityManager em, Runnable action) {
        execute(em, () -> {
            action.run();
            return null;
        });
    }

    private static RuntimeException enrich(RuntimeException ex) {
        String detail = rootMessage(ex);
        if (detail == null || detail.isBlank()) {
            return ex;
        }
        if (ex.getMessage() != null && ex.getMessage().contains(detail)) {
            return ex;
        }
        return new IllegalStateException(detail, ex);
    }

    private static String rootMessage(Throwable throwable) {
        Throwable current = throwable;
        String lastMessage = null;

        while (current != null) {
            if (current.getMessage() != null && !current.getMessage().isBlank()) {
                lastMessage = current.getMessage();
            }
            current = current.getCause();
        }

        return lastMessage;
    }
}
