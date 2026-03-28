#!/bin/bash
set -e

echo ">>> Iniciando WildFly em background para configurar datasource..."
$JBOSS_HOME/bin/standalone.sh -b 0.0.0.0 &
WILDFLY_PID=$!

echo ">>> Aguardando WildFly subir..."
until $JBOSS_HOME/bin/jboss-cli.sh --connect --command="ls" &>/dev/null; do
    sleep 3
done

echo ">>> WildFly pronto. Verificando datasource..."

# Só configura o datasource se ainda não existir
DS_EXISTS=$($JBOSS_HOME/bin/jboss-cli.sh --connect --command="/subsystem=datasources/data-source=PostgreSQLDS:read-attribute(name=enabled)" 2>&1 || true)

if echo "$DS_EXISTS" | grep -q "WFLYCTL0216"; then
    echo ">>> Datasource não encontrado. Configurando..."

    $JBOSS_HOME/bin/jboss-cli.sh --connect --command="
        data-source add
        --name=PostgreSQLDS
        --jndi-name=java:jboss/datasources/PostgreSQLDS
        --driver-name=postgresql
        --connection-url=jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
        --user-name=${DB_USER}
        --password=${DB_PASSWORD}
        --valid-connection-checker-class-name=org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLValidConnectionChecker
        --exception-sorter-class-name=org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLExceptionSorter
        --enabled=true"

    echo ">>> Datasource configurado com sucesso!"
else
    echo ">>> Datasource já existe, pulando configuração."
fi

echo ">>> Mantendo WildFly em foreground..."
wait $WILDFLY_PID
