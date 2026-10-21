# ==============================================================================
# Dockerfile - Telecom Tower Maintenance Portal
# Slim Runtime: Eclipse Temurin 21 JRE
# ==============================================================================

# 1. Base Image: Lightweight Eclipse Temurin OpenJDK 21 JRE
#    Provides the minimal runtime environment required for Java 21 without compiler tools.
FROM eclipse-temurin:21-jre

# 2. Metadata Labels
LABEL maintainer="Telecom Tower Operations DevOps Team"
LABEL description="Telecom Tower Maintenance Portal Spring Boot WAR Container"

# 3. Environment Variables: Configurable runtime parameters
#    JAVA_OPTS allows passing JVM tuning flags (memory limits, GC)
#    SPRING_PROFILES_ACTIVE defines the active Spring profile
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC" \
    SPRING_PROFILES_ACTIVE="default" \
    SERVER_PORT=8080

# 4. Set Application Working Directory inside the container
WORKDIR /app

# 5. Build Argument: Path to the packaged WAR artifact with default wildcard pattern
ARG WAR_FILE=target/telecom-tower-maintenance-portal-*.war

# 6. Copy the packaged WAR artifact from the host target directory into /app/app.war
COPY ${WAR_FILE} /app/app.war

# 7. Security Best Practice: Run container as a dedicated non-root user
#    Creates a system group 'appgroup' and system user 'appuser', then sets ownership of /app
RUN groupadd -r appgroup && useradd -r -g appgroup -d /app -s /sbin/nologin appuser \
    && chown -R appuser:appgroup /app

# 8. Switch to the non-root user
USER appuser

# 9. Document the container listening port (Spring Boot standard port 8080)
EXPOSE 8080

# 10. Container Health Check:
#     Polls the Spring Boot Actuator health endpoint periodically.
#     Uses curl with a 40-second start grace period to allow JVM warm-up.
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD curl -f -s http://localhost:8080/actuator/health || exit 1

# 11. Entrypoint: Launch Spring Boot application using exec form with JAVA_OPTS expansion
ENTRYPOINT ["sh", "-c", "exec java ${JAVA_OPTS} -jar /app/app.war"]
