#!/bin/bash

# Generate frontend env-config.js from .env file

set -e

# Load .env file
if [ -f .env ]; then
    source .env
else
    echo "Error: .env file not found"
    exit 1
fi

# Generate env-config.js
cat > frontend/env-config.js << EOF
// Environment configuration for frontend
// Auto-generated from .env file - DO NOT EDIT MANUALLY

window.ENV = {
    API_BASE_URL: '${API_BASE_URL}',
    ENVIRONMENT: '${SPRING_PROFILE}'
};
EOF

echo "✅ Generated frontend/env-config.js from .env"
