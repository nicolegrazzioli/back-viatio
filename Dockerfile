# Estágio 1: Construção (Build) da aplicação
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
# Baixa as dependências para acelerar builds futuros no Render
RUN mvn dependency:go-offline -B
COPY src ./src
# Compila e gera o arquivo .jar (ignorando testes para acelerar o deploy)
RUN mvn package -DskipTests

# Estágio 2: Execução (Runtime) da aplicação
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
# Copia apenas o arquivo .jar gerado para ficar bem leve
COPY --from=build /app/target/*.jar app.jar
# Porta padrão que o Render expõe
EXPOSE 8080
# Comando para iniciar o backend
ENTRYPOINT ["java", "-jar", "app.jar"]
