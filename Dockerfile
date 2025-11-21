# Usar una imagen más estable y ampliamente disponible
FROM maven:3.8.6-openjdk-17 AS build
WORKDIR /app

# Copiar solo los archivos necesarios primero (para mejor caching)
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw

# Descargar dependencias (esto se cachea si el pom.xml no cambia)
RUN ./mvnw dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Compilar la aplicación
RUN ./mvnw clean package -DskipTests

# Imagen final más ligera
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copiar el WAR construido
COPY --from=build /app/target/base-0.0.1-SNAPSHOT.war app.war

# Crear usuario no-root para seguridad
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Exponer puerto
EXPOSE 8080

# Ejecutar la aplicación con perfil de producción
ENTRYPOINT ["java", "-jar", "app.war", "--spring.profiles.active=prod"]
