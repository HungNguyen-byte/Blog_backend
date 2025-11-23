FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml .
COPY src ./src

RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:21-jdk
WORKDIR /app

# create an unprivileged user for runtime
RUN addgroup -S app && adduser -S app -G app

# copy built jar (wildcard to handle varying jar names)
COPY --from=build /workspace/target/*.jar /app/app.jar

# set permissions and uploads directory
RUN chmod 755 /app/app.jar \
 && mkdir -p /uploads \
 && chown -R app:app /uploads /app/app.jar

EXPOSE 8080

# keep env names but leave values empty — supply at runtime for security
ENV SPRING_DATA_MONGODB_URI=
ENV JWT_SECRET=

VOLUME ["/uploads"]

USER app

ENTRYPOINT ["java", "-jar", "/app/app.jar"]