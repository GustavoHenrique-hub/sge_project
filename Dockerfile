# =============================================================
# Stage 1: Build com Maven + Java 17
# =============================================================
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copia o pom.xml primeiro para aproveitar o cache de dependências
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia o restante do código e builda
COPY src ./src
RUN mvn clean package -DskipTests

# =============================================================
# Stage 2: Runtime com WildFly (JDK 17)
# =============================================================
FROM quay.io/wildfly/wildfly:latest-jdk17

USER root

# Baixa o driver JDBC do PostgreSQL e registra o módulo no WildFly
# (isso pode ser feito no build pois não depende de variáveis de ambiente)
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

# Limpa histórico de configuração gerado durante o build
RUN rm -rf $JBOSS_HOME/standalone/configuration/standalone_xml_history/ \
           $JBOSS_HOME/standalone/log/*

# Copia o WAR gerado no Stage 1
# ⚠️  Ajuste o nome do .war conforme o <artifactId> e <version> do seu pom.xml
COPY --from=build /app/target/*.war $JBOSS_HOME/standalone/deployments/

# Copia o entrypoint que configura o datasource em runtime (com as envs disponíveis)
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

USER jboss

EXPOSE 8080

# O entrypoint configura o datasource usando as variáveis de ambiente do Render,
# depois mantém o WildFly rodando em foreground
ENTRYPOINT ["/entrypoint.sh"]