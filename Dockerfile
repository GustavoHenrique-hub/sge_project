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
# Stage 2: Runtime com Tomcat 10
# =============================================================
FROM tomcat:10.1-jdk17

RUN rm -rf $CATALINA_HOME/webapps/*

COPY --from=build /app/target/sge_project-1.0-SNAPSHOT.war \
     $CATALINA_HOME/webapps/ROOT.war

EXPOSE 8080

# O JPAProducer le DB_URL/DB_USER/DB_PASSWORD ou DB_HOST/DB_PORT/DB_NAME do ambiente.
CMD ["catalina.sh", "run"]
