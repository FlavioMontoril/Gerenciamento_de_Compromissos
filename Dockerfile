# Estágio de Build
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copia apenas o pom.xml primeiro para aproveitar o cache das dependências
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o código fonte e realiza o build
COPY src ./src
RUN mvn clean package -DskipTests

# Estágio de Execução
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Cria um grupo e um usuário não-root para segurança em produção
RUN addgroup -S spring && adduser -S spring -G spring

# Define o dono da pasta da aplicação para o usuário não-root
RUN chown -R spring:spring /app

# Copia o JAR gerado no estágio anterior com a permissão correta
COPY --from=build --chown=spring:spring /app/target/*.jar app.jar

# Define o usuário que executará a aplicação
USER spring

# Expõe a porta padrão do Spring Boot
EXPOSE 8080

# Comando para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
