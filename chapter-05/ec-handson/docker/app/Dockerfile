FROM mcr.microsoft.com/devcontainers/base:ubuntu-24.04

SHELL ["/bin/bash", "-o", "pipefail", "-c"]

# 作業に使うコマンド
# bubblewrapとsocatは、Claude Codeのサンドボックスが使う
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        bubblewrap \
        ca-certificates \
        curl \
        gnupg \
        jq \
        postgresql-client \
        python3 \
        python3-openpyxl \
        ripgrep \
        socat \
    && rm -rf /var/lib/apt/lists/*

# JDK 21（Eclipse Temurin、Adoptiumのaptリポジトリ）
ARG ADOPTIUM_KEY_FINGERPRINT=3B04D753C9050D9A5D343F39843C48A565F8F04B
ARG TEMURIN_VERSION=21.0.12.1.0+1-0
RUN install -d -m 0755 /etc/apt/keyrings \
    && curl -fsSL -o /etc/apt/keyrings/adoptium.asc \
        https://packages.adoptium.net/artifactory/api/gpg/key/public \
    && test "$(gpg --show-keys --with-colons /etc/apt/keyrings/adoptium.asc | awk -F: '/^fpr:/ { print $10; exit }')" = "${ADOPTIUM_KEY_FINGERPRINT}" \
    && echo "deb [signed-by=/etc/apt/keyrings/adoptium.asc] https://packages.adoptium.net/artifactory/deb $(. /etc/os-release && echo "${VERSION_CODENAME}") main" \
        > /etc/apt/sources.list.d/adoptium.list \
    && apt-get update \
    && apt-get install -y --no-install-recommends "temurin-21-jdk=${TEMURIN_VERSION}" \
    && rm -rf /var/lib/apt/lists/* \
    && ln -s "/usr/lib/jvm/temurin-21-jdk-$(dpkg --print-architecture)" /usr/lib/jvm/temurin-21
ENV JAVA_HOME=/usr/lib/jvm/temurin-21

# Maven
ARG MAVEN_VERSION=3.9.16
ARG MAVEN_SHA512=831a8591fe20c8243b1dbe7d71e3244f31d1665b0804b2e825e38cbbe5ce0cafb8338851f90780735568773e0a6cd07bbec107cda0b896b008b861075358b6f6
RUN curl -fsSL -o /tmp/maven.tar.gz \
        "https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/${MAVEN_VERSION}/apache-maven-${MAVEN_VERSION}-bin.tar.gz" \
    && echo "${MAVEN_SHA512}  /tmp/maven.tar.gz" | sha512sum -c - \
    && mkdir -p /opt/maven \
    && tar -xzf /tmp/maven.tar.gz -C /opt/maven --strip-components=1 \
    && rm /tmp/maven.tar.gz \
    && ln -s /opt/maven/bin/mvn /usr/local/bin/mvn

# Javaの言語サーバー（Eclipse JDT Language Server）
ARG JDTLS_VERSION=1.61.0
ARG JDTLS_BUILD=202609031315
ARG JDTLS_SHA256=338e7e73d61836651ba2453919a0d34fa763eb4e7c03342092309bffb8934c64
RUN curl -fsSL -o /tmp/jdtls.tar.gz \
        "https://download.eclipse.org/jdtls/milestones/${JDTLS_VERSION}/jdt-language-server-${JDTLS_VERSION}-${JDTLS_BUILD}.tar.gz" \
    && echo "${JDTLS_SHA256}  /tmp/jdtls.tar.gz" | sha256sum -c - \
    && mkdir -p /opt/jdtls \
    && tar -xzf /tmp/jdtls.tar.gz -C /opt/jdtls \
    && rm /tmp/jdtls.tar.gz
COPY jdtls /usr/local/bin/jdtls

# Claude CodeはREADMEの手順で利用者が導入する。
USER vscode
ENV PATH=/home/vscode/.local/bin:${PATH}

# 名前付きボリュームを重ねるディレクトリは、vscodeユーザーの所有で作っておく
RUN mkdir -p /home/vscode/.claude /home/vscode/.m2 /home/vscode/.cache/jdtls

# macOSの所有者IDとvscodeのIDが異なるため、既知の共有先だけをGitで信頼する。
RUN git config --global --add safe.directory /workspace

# 日本語を含む出力（商品名など）が文字化けしないよう、UTF-8のロケールにする
ENV LANG=C.UTF-8 \
    LC_ALL=C.UTF-8
