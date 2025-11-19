#!/bin/bash

# Health Check Test Script
# Tests the health endpoint of the Expense Management API

BASE_URL="${1:-http://localhost:8080}"

echo "=========================================="
echo "Testing Health Endpoint"
echo "=========================================="
echo ""

HEALTH_URL="$BASE_URL/api/health"

echo "Testing endpoint: $HEALTH_URL"
echo ""

# Make request to health endpoint
HTTP_CODE=$(curl -s -o /tmp/health_response.json -w "%{http_code}" "$HEALTH_URL")

if [ $? -eq 0 ] && [ "$HTTP_CODE" = "200" ]; then
    echo "=========================================="
    echo "Health Check: SUCCESS"
    echo "=========================================="
    echo ""
    
    echo "Response:"
    cat /tmp/health_response.json | python3 -m json.tool 2>/dev/null || cat /tmp/health_response.json
    echo ""
    
    STATUS=$(cat /tmp/health_response.json | grep -o '"status":"[^"]*"' | cut -d'"' -f4)
    
    if [ "$STATUS" = "UP" ]; then
        echo "✓ Application is healthy and running"
        rm -f /tmp/health_response.json
        exit 0
    else
        echo "✗ Application status is not UP"
        rm -f /tmp/health_response.json
        exit 1
    fi
else
    echo "=========================================="
    echo "Health Check: FAILED"
    echo "=========================================="
    echo ""
    
    if [ $? -ne 0 ]; then
        echo "✗ Cannot connect to server at $HEALTH_URL"
        echo ""
        echo "Possible causes:"
        echo "  - Application is not running"
        echo "  - Wrong URL or port"
        echo "  - Firewall blocking connection"
    else
        echo "✗ HTTP Status Code: $HTTP_CODE"
        echo ""
        echo "Response:"
        cat /tmp/health_response.json 2>/dev/null
    fi
    
    echo ""
    rm -f /tmp/health_response.json
    exit 1
fi
