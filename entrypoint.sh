#!/bin/bash
set -e

echo ">>> Iniciando WildFly..."
$JBOSS_HOME/bin/standalone.sh -b 0.0.0.0 &
WILDFLY_PID=$!

echo ">>> Aguardando WildFly ficar pronto..."
until $JBOSS_HOME/bin/jboss-cli.sh --connect --command="ls" &>/dev/null; do
    sleep 3
done

echo ">>> WildFly pronto. Verificando datasource..."

DS_EXISTS=$($JBOSS_HOME/bin/jboss-cli.sh --connect \
    --command="/subsystem=datasources/data-source=sgeDS:read-attribute(name=enabled)" 2>&1 || true)

if echo "$DS_EXISTS" | grep -q "WFLYCTL0216\|not found\|WFLYCTL0030"; then
    echo ">>> Criando datasource sgeDS..."

    $JBOSS_HOME/bin/jboss-cli.sh --connect << EOF
data-source add \
    --name=sgeDS \
    --jndi-name=java:/jdbc/sgeDS \
    --driver-name=postgresql \
    --connection-url=jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME} \
    --user-name=${DB_USER} \
    --password=${DB_PASSWORD} \
    --valid-connection-checker-class-name=org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLValidConnectionChecker \
    --exception-sorter-class-name=org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLExceptionSorter \
    --enabled=true
EOF

    echo ">>> Datasource criado com sucesso!"
else
    echo ">>> Datasource já existe, pulando."
fi

echo ">>> Mantendo WildFly em foreground..."
wait $WILDFLY_PID