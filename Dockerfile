# ---- Build stage ----
# CRITICAL: This MUST be a JDK image so Maven has a compiler!
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B --no-transfer-progress

COPY src/ src/
RUN ./mvnw package -DskipTests -B --no-transfer-progress

RUN java -Djarmode=layertools -jar target/*.jar extract --destination extracted


# ---- Model Downloader stage ----
FROM alpine:latest AS model-downloader
WORKDIR /models
RUN apk add --no-cache curl

RUN curl -L "https://huggingface.co/sentence-transformers/all-MiniLM-L6-v2/resolve/main/onnx/model.onnx" -o model.onnx
RUN curl -L "https://huggingface.co/sentence-transformers/all-MiniLM-L6-v2/resolve/main/tokenizer.json" -o tokenizer.json
RUN curl -L "https://huggingface.co/sentence-transformers/all-MiniLM-L6-v2/resolve/main/tokenizer_config.json" -o tokenizer_config.json


# ---- Run stage ----
# RECOMMENDED: Use the Ubuntu-based JRE image here to support ONNX native C++ bindings
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser

# Copy layers in order of least-to-most frequently changed
COPY --from=builder /app/extracted/dependencies/ ./
COPY --from=builder /app/extracted/spring-boot-loader/ ./
COPY --from=builder /app/extracted/snapshot-dependencies/ ./
COPY --from=builder /app/extracted/application/ ./

# Place models in BOOT-INF/classes so they exist on the classpath at runtime
COPY --from=model-downloader /models/model.onnx ./BOOT-INF/classes/models/
COPY --from=model-downloader /models/tokenizer.json ./BOOT-INF/classes/models/
COPY --from=model-downloader /models/tokenizer_config.json ./BOOT-INF/classes/models/

RUN chown -R appuser:appgroup /app
USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "org.springframework.boot.loader.launch.JarLauncher"]