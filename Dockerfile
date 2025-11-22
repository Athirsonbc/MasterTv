# Imagem com JDK
FROM openjdk:17-slim

# Instalar dependências essenciais
RUN apt-get update && apt-get install -y wget unzip curl

# Instalar Android SDK command-line
RUN mkdir -p /android-sdk
RUN cd /android-sdk && \
    wget https://dl.google.com/android/repository/commandlinetools-linux-9123335_latest.zip -O cmdtools.zip && \
    unzip cmdtools.zip -d cmdtools && rm cmdtools.zip

ENV ANDROID_HOME="/android-sdk"
ENV PATH="$PATH:/android-sdk/cmdtools/bin:/android-sdk/platform-tools:/android-sdk/build-tools/33.0.1"

# Aceitar licenças automaticamente
RUN yes | /android-sdk/cmdtools/bin/sdkmanager --sdk_root=/android-sdk "platform-tools" "platforms;android-34" "build-tools;33.0.1"

# Copiar projeto
WORKDIR /app
COPY . .

# Dar permissão ao gradlew
RUN chmod +x gradlew

# Compilar APK
RUN ./gradlew assembleRelease

# Expor APK final
RUN mkdir -p /app/output && cp app/build/outputs/apk/release/app-release.apk /app/output/mastertv.apk

CMD ["bash", "-c", "echo 'APK gerado em /app/output/mastertv.apk'; sleep infinity"]
