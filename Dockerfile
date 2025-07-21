FROM openjdk:21-jdk-slim

WORKDIR /app

# Copia solo los archivos necesarios para que Maven pueda instalar
COPY pom.xml mvnw ./
COPY .mvn .mvn

# Descarga dependencias antes para aprovechar caché
RUN ./mvnw dependency:go-offline

# Ahora copia todo el código
COPY src ./src

# Construye el proyecto (sin tests)
RUN ./mvnw clean install -DskipTests

# Exponer el puerto que usa Spring Boot
EXPOSE 8080

# Comando para iniciar la app
CMD ["java", "-jar", "target/sistemaproyecto-0.0.1.jar"]
