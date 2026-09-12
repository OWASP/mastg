FROM python:3.14-slim

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
    git \
    libxml2-dev \
    libxslt1-dev \
    zlib1g-dev \
    openssh-client \
    ca-certificates \
    build-essential build-essential \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /workspace/mastg
