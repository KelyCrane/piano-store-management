# ===== 多阶段构建：Maven 编译 + JRE 运行 =====
FROM maven:3.8-openjdk-8-slim AS builder
WORKDIR /app
COPY pom.xml .
# 先下载依赖（利用 Docker 缓存层）
RUN mvn dependency:go-offline -B -q || true
COPY src ./src
COPY frontend/dist ./frontend/dist
RUN mvn package -Pcloud -DskipTests -q

FROM openjdk:8-jre-slim
WORKDIR /app
# 创建上传目录
RUN mkdir -p /app/uploads
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8088
ENTRYPOINT ["java", "-jar", "app.jar"]
