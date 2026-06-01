FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", \
  "-Dspring.datasource.url=jdbc:postgresql://chatsdb_k39h_user:wSpRBExomVw9IRcAswle9MzFwFEChJQ2@dpg-d8elepmk1jcs73a3o0d0-a.oregon-postgres.render.com:5432/chatsdb_k39h", \
  "-Dspring.datasource.username=chatsdb_k39h_user", \
  "-Dspring.datasource.password=wSpRBExomVw9IRcAswle9MzFwFEChJQ2", \
  "-Dspring.mail.host=smtp.gmail.com", \
  "-Dspring.mail.port=587", \
  "-Dspring.mail.username=chiragofficial1207@gmail.com", \
  "-Dspring.mail.password=zqtiitawrpiizuje", \
  "-Dspring.mail.properties.mail.smtp.auth=true", \
  "-Dspring.mail.properties.mail.smtp.starttls.enable=true", \
  "-Dspp.base-url=https://Dravon-chat.onrender.com", \
  "-jar", "app.jar"]