FROM mcr.microsoft.com/devcontainers/base:trixie

# Note: If the requiremtnes change for this image, we need to update it in all MAS sibling projects:
#
# - https://github.com/OWASP/mas-website
# - https://github.com/OWASP/masvs
# - https://github.com/OWASP/maswe
# - https://github.com/OWASP/mastg

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

