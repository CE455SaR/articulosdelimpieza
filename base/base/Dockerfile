FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# Copiar archivos de Maven
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw

# Descargar dependencias
RUN ./mvnw dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Compilar la aplicación
RUN ./mvnw clean package -DskipTests

# Imagen final
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copiar el WAR construido
COPY --from=build /app/target/base-0.0.1-SNAPSHOT.war app.war

# Exponer puerto
EXPOSE 8080

# Ejecutar la aplicación con perfil de producción
ENTRYPOINT ["java", "-jar", "app.war", "--spring.profiles.active=prod"]