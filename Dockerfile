# 1단계: 화면 빌드
FROM node:24-alpine AS frontend
WORKDIR /frontend

COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci

COPY frontend/ ./
RUN npm run build

# 2단계: 애플리케이션 빌드
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

COPY gradle gradle
COPY gradlew build.gradle settings.gradle ./
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

COPY src src
# 빌드된 화면을 정적 리소스 위치에 넣어 하나의 jar 로 묶는다.
# 화면과 API 가 같은 주소에서 서비스되므로 CORS 설정이 필요 없다.
COPY --from=frontend /frontend/dist src/main/resources/static

RUN ./gradlew bootJar --no-daemon -x test

# 3단계: 실행
FROM eclipse-temurin:21-jre
WORKDIR /app

RUN useradd --create-home --shell /bin/bash app
USER app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
