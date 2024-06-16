# Usar una imagen base de Maven para la compilación
FROM maven:3.9.7-amazoncorretto-17-debian AS build

# Establecer el directorio de trabajo dentro del contenedor
WORKDIR /app


# Copiar el JAR compilado desde el contenedor de construcción
COPY ./target/*.jar app.jar

# Exponer el puerto en el que la aplicación se ejecuta
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]

