FROM mcr.microsoft.com/devcontainers/base:trixie

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
    python3 \
    python3-pip \
    python3-venv \
    git \
    libxml2-dev \
    libxslt1-dev \
    zlib1g-dev \
    build-essential build-essential \
    && rm -rf /var/lib/apt/lists/*

