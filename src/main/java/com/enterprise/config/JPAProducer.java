package com.enterprise.config;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class JPAProducer {
    private EntityManagerFactory emf;

    private EntityManagerFactory getEmf() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("meuPU", buildOverrides());
        }
        return emf;
    }

    @Produces
    @RequestScoped
    public EntityManager em() {
        return getEmf().createEntityManager();
    }

    public void close(@Disposes EntityManager em) {
        if (em.isOpen()) {
            em.close();
        }
    }

    @PreDestroy
    public void destroy() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    private Map<String, Object> buildOverrides() {
        Map<String, Object> properties = new HashMap<>();

        String dbUrl = readConfig("DB_URL");
        if (dbUrl == null) {
            dbUrl = buildJdbcUrl();
        }

        putIfPresent(properties, "jakarta.persistence.jdbc.url", dbUrl);
        putIfPresent(properties, "jakarta.persistence.jdbc.user", readConfig("DB_USER"));
        putIfPresent(properties, "jakarta.persistence.jdbc.password", readConfig("DB_PASSWORD"));
        putIfPresent(properties, "hibernate.hbm2ddl.auto", readConfig("HIBERNATE_HBM2DDL_AUTO"));
        putIfPresent(properties, "hibernate.show_sql", readConfig("HIBERNATE_SHOW_SQL"));
        putIfPresent(properties, "hibernate.use_sql_comments", readConfig("HIBERNATE_USE_SQL_COMMENTS"));

        return properties;
    }

    private String buildJdbcUrl() {
        String host = readConfig("DB_HOST");
        String port = readConfig("DB_PORT");
        String database = readConfig("DB_NAME");
        if (host == null || database == null) {
            return null;
        }
        return "jdbc:postgresql://" + host + ":" + (port == null ? "5432" : port) + "/" + database;
    }

    private String readConfig(String key) {
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue;
        }

        String envValue = System.getenv(key);
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }
        return null;
    }

    private void putIfPresent(Map<String, Object> properties, String key, String value) {
        if (value != null && !value.isBlank()) {
            properties.put(key, value);
        }
    }
}
