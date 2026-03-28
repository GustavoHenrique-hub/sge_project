# =============================================================
# Stage 1: Build com Maven + Java 17
# =============================================================
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

# =============================================================
# Stage 2: Runtime com WildFly (JDK 17)
# =============================================================
FROM quay.io/wildfly/wildfly:latest-jdk17

USER root

# Baixa o driver JDBC do PostgreSQL
ENV POSTGRES_DRIVER_VERSION=42.7.3
RUN curl -L https://repo1.maven.org/maven2/org/postgresql/postgresql/${POSTGRES_DRIVER_VERSION}/postgresql-${POSTGRES_DRIVER_VERSION}.jar \
    -o /tmp/postgresql.jar

# Registra o módulo e o driver JDBC no WildFly durante o build
# (não depende de variáveis de ambiente, pode ser feito aqui)
RUN /bin/sh -c ' \
    $JBOSS_HOME/bin/standalone.sh & \
    sleep 15 && \
    $JBOSS_HOME/bin/jboss-cli.sh --connect --command=" \
        module add \
        --name=org.postgresql \
        --resources=/tmp/postgresql.jar \
        --dependencies=javax.api,javax.transaction.api" && \
    $JBOSS_HOME/bin/jboss-cli.sh --connect --command=" \
        /subsystem=datasources/jdbc-driver=postgresql:add( \
        driver-name=postgresql, \
        driver-module-name=org.postgresql, \
        driver-class-name=org.postgresql.Driver)" && \
    $JBOSS_HOME/bin/jboss-cli.sh --connect --command=:shutdown && \
    rm -f /tmp/postgresql.jar \
'

# Limpa artefatos temporários do build
RUN rm -rf $JBOSS_HOME/standalone/configuration/standalone_xml_history/ \
           $JBOSS_HOME/standalone/log/*

# Copia o WAR gerado — artifactId=sge_project, version=1.0-SNAPSHOT
COPY --from=build /app/target/sge_project-1.0-SNAPSHOT.war $JBOSS_HOME/standalone/deployments/ROOT.war

# Copia o entrypoint que configura o datasource em runtime
COPY entrypoint.sh /entrypoint.sh

# Garante permissões corretas para o usuário jboss em todos os diretórios necessários
RUN chown -R jboss:jboss $JBOSS_HOME/standalone \
                         $JBOSS_HOME/modules \
                         /entrypoint.sh && \
    chmod -R 755 $JBOSS_HOME/standalone && \
    chmod +x /entrypoint.sh

USER jboss

EXPOSE 8080

ENTRYPOINT ["/entrypoint.sh"]