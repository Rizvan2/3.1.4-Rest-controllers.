FROM eclipse-temurin:21-jdk

# Устанавливаем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем .jar файл в контейнер
COPY target/*.jar app.jar

# Указываем команду запуска
ENTRYPOINT ["java", "-jar", "app.jar"]