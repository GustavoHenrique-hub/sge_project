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

# Baixa o driver JDBC e registra o módulo (sem datasource — não precisa de envs)
ENV POSTGRES_DRIVER_VERSION=42.7.3
RUN curl -L https://repo1.maven.org/maven2/org/postgresql/postgresql/${POSTGRES_DRIVER_VERSION}/postgresql-${POSTGRES_DRIVER_VERSION}.jar \
    -o /tmp/postgresql.jar

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

RUN rm -rf $JBOSS_HOME/standalone/configuration/standalone_xml_history/ \
           $JBOSS_HOME/standalone/log/*

# Copia o WAR
# ⚠️ Ajuste o nome do .war conforme o <artifactId> e <version> do seu pom.xml
COPY --from=build /app/target/*.war $JBOSS_HOME/standalone/deployments/

# Copia o script CLI que configura o datasource na inicialização
# O WildFly executa este arquivo automaticamente antes de abrir portas
COPY postconfigure.cli $JBOSS_HOME/extensions/postconfigure.cli

RUN chown -R jboss:jboss $JBOSS_HOME/extensions

USER jboss

EXPOSE 8080

# --properties e -b garantem que o WildFly processa o postconfigure.cli e aceita conexões externas
CMD ["/opt/jboss/wildfly/bin/standalone.sh", \
     "-b", "0.0.0.0", \
     "--start-mode=normal"]