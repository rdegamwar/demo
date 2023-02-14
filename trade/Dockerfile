FROM FROM openjdk:17-jre-slim
EXPOSE 8080

RUN mkdir /app

COPY build/libs/*.jar /app/

RUN chmod 775 /app/*.*

CMD java -jar trade-0.0.1.SNAPSHOT.jar