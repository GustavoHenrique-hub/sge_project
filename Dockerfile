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

# Durante o build: configura módulo, driver, datasource e remove o welcome-content handler
RUN /bin/sh -c ' \
    $JBOSS_HOME/bin/standalone.sh \
        -Ddb.host=localhost \
        -Ddb.port=5432 \
        -Ddb.name=placeholder \
        -Ddb.user=placeholder \
        -Ddb.password=placeholder & \
    sleep 20 && \
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
        --name=sgeDS \
        --jndi-name=java:/jdbc/sgeDS \
        --driver-name=postgresql \
        --connection-url=jdbc:postgresql://\${db.host}:\${db.port}/\${db.name} \
        --user-name=\${db.user} \
        --password=\${db.password} \
        --valid-connection-checker-class-name=org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLValidConnectionChecker \
        --exception-sorter-class-name=org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLExceptionSorter \
        --enabled=true" && \
    $JBOSS_HOME/bin/jboss-cli.sh --connect --command=" \
        /subsystem=undertow/server=default-server/host=default-host/location=\/:remove" && \
    $JBOSS_HOME/bin/jboss-cli.sh --connect --command=" \
        /subsystem=undertow/configuration=handler/file=welcome-content:remove" && \
    $JBOSS_HOME/bin/jboss-cli.sh --connect --command=:shutdown \
'

RUN rm -rf $JBOSS_HOME/standalone/configuration/standalone_xml_history/ \
           $JBOSS_HOME/standalone/log/* \
           /tmp/postgresql.jar

# WAR renomeado para ROOT.war → disponível na raiz /
COPY --from=build /app/target/sge_project-1.0-SNAPSHOT.war \
     $JBOSS_HOME/standalone/deployments/ROOT.war

# Corrige permissões para o usuário jboss
RUN chown -R jboss:jboss $JBOSS_HOME/standalone $JBOSS_HOME/modules && \
    chmod -R 755 $JBOSS_HOME/standalone

USER jboss

EXPOSE 8080

# Em runtime: passa as variáveis de ambiente do Render como system properties do Java
CMD ["/bin/sh", "-c", \
     "/opt/jboss/wildfly/bin/standalone.sh \
      -b 0.0.0.0 \
      -Ddb.host=${DB_HOST} \
      -Ddb.port=${DB_PORT} \
      -Ddb.name=${DB_NAME} \
      -Ddb.user=${DB_USER} \
      -Ddb.password=${DB_PASSWORD}"]