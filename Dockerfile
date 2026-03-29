# ---------- Build stage ----------
FROM sapmachine:21-jdk-ubuntu-24.04 AS builder

WORKDIR /opt/app

# Install Maven
RUN apt-get update && apt-get install -y maven --no-install-recommends && rm -rf /var/lib/apt/lists/*

# Copy Maven wrapper & config first (for layer caching)
COPY pom.xml ./

# Copy source
COPY ./src ./src

# Build the app
RUN mvn clean package -DskipTests --no-transfer-progress

# ---------- Runtime stage ----------
FROM sapmachine:21-jre-ubuntu-24.04 AS final

WORKDIR /opt/app
EXPOSE 8080

# Setup an app user so the container doesn't run as the root user
RUN useradd app && chown app:app /opt/app
USER app

COPY --from=builder /opt/app/target/*.jar app.jar
COPY --from=builder /opt/app/src/main/webapp /opt/app/webapp
ENTRYPOINT ["java", "-jar", "/opt/app/app.jar"]