#!/bin/bash

# AWS Deployment Script for Appointment Management System
# This script automates the deployment to AWS Elastic Beanstalk

set -e  # Exit on error

echo "=========================================="
echo "AWS Deployment Script"
echo "Appointment Management System"
echo "=========================================="
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if AWS CLI is installed
if ! command -v aws &> /dev/null; then
    echo -e "${RED}Error: AWS CLI is not installed${NC}"
    echo "Please install AWS CLI: https://aws.amazon.com/cli/"
    exit 1
fi

# Check if EB CLI is installed
if ! command -v eb &> /dev/null; then
    echo -e "${RED}Error: EB CLI is not installed${NC}"
    echo "Please install EB CLI: pip install awsebcli"
    exit 1
fi

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo -e "${RED}Error: Maven is not installed${NC}"
    echo "Please install Maven"
    exit 1
fi

echo -e "${YELLOW}Step 1: Building application...${NC}"
mvn clean package -DskipTests
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Build successful${NC}"
else
    echo -e "${RED}✗ Build failed${NC}"
    exit 1
fi

echo ""
echo -e "${YELLOW}Step 2: Checking EB initialization...${NC}"
if [ ! -d ".elasticbeanstalk" ]; then
    echo -e "${YELLOW}EB not initialized. Initializing...${NC}"
    eb init
else
    echo -e "${GREEN}✓ EB already initialized${NC}"
fi

echo ""
echo -e "${YELLOW}Step 3: Deploying to Elastic Beanstalk...${NC}"
eb deploy

if [ $? -eq 0 ]; then
    echo ""
    echo -e "${GREEN}=========================================="
    echo "✓ Deployment successful!"
    echo "==========================================${NC}"
    echo ""
    echo "To view your application:"
    echo "  eb open"
    echo ""
    echo "To view logs:"
    echo "  eb logs"
    echo ""
    echo "To check status:"
    echo "  eb status"
else
    echo -e "${RED}✗ Deployment failed${NC}"
    exit 1
fi
