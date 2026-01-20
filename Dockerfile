FROM python:3.10-slim

# Set working directory
WORKDIR /app

# Install system dependencies
RUN apt-get update && apt-get install -y --no-install-recommends \
    libxml2-dev \
    libxslt1-dev \
    gcc \
    && rm -rf /var/lib/apt/lists/*

# Copy requirements
COPY src/scripts/requirements.txt requirements.txt

# Install python dependencies
RUN pip install --no-cache-dir -r requirements.txt

# Copy the source code
COPY . .

# Set default environment variable for local MASVS path (can be overridden)
ENV MASVS_LOCAL_PATH="/app/masvs.yaml"

# Default command
CMD ["python3", "src/scripts/yaml_to_excel.py"]
