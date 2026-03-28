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

# Usuário root para instalar o driver JDBC
USER root

# Baixa o driver JDBC do PostgreSQL
ENV POSTGRES_DRIVER_VERSION=42.7.3
RUN curl -L https://repo1.maven.org/maven2/org/postgresql/postgresql/${POSTGRES_DRIVER_VERSION}/postgresql-${POSTGRES_DRIVER_VERSION}.jar \
    -o /tmp/postgresql.jar

# Inicia o WildFly em background, instala o driver e o datasource via CLI,
# depois encerra o servidor (a configuração fica persistida no standalone.xml)
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
    $JBOSS_HOME/bin/jboss-cli.sh --connect --command=" \
        data-source add \
        --name=PostgreSQLDS \
        --jndi-name=java:jboss/datasources/PostgreSQLDS \
        --driver-name=postgresql \
        --connection-url=jdbc:postgresql://\${env.DB_HOST}:\${env.DB_PORT}/\${env.DB_NAME} \
        --user-name=\${env.DB_USER} \
        --password=\${env.DB_PASSWORD} \
        --valid-connection-checker-class-name=org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLValidConnectionChecker \
        --exception-sorter-class-name=org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLExceptionSorter \
        --enabled=true" && \
    $JBOSS_HOME/bin/jboss-cli.sh --connect --command=:shutdown && \
    rm -f /tmp/postgresql.jar \
'

# Limpa histórico de configuração gerado durante o build
RUN rm -rf $JBOSS_HOME/standalone/configuration/standalone_xml_history/ \
           $JBOSS_HOME/standalone/log/*

# Copia o WAR gerado no Stage 1
# ⚠️  Ajuste o nome do .war conforme o <artifactId> e <version> do seu pom.xml
COPY --from=build /app/target/*.war $JBOSS_HOME/standalone/deployments/

USER jboss

EXPOSE 8080

# Bind em 0.0.0.0 é obrigatório no Render para o tráfego externo funcionar
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0"]
