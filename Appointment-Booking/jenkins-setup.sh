#!/bin/bash

# Jenkins Setup Script for Appointment Management System
# This script helps automate Jenkins configuration

set -e

echo "=========================================="
echo "Jenkins Setup Helper Script"
echo "Appointment Management System"
echo "=========================================="
echo ""

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Check if Jenkins is running
check_jenkins() {
    echo -e "${YELLOW}Checking if Jenkins is running...${NC}"
    if curl -s http://localhost:8080 > /dev/null; then
        echo -e "${GREEN}✓ Jenkins is running${NC}"
        return 0
    else
        echo -e "${RED}✗ Jenkins is not running${NC}"
        echo "Please start Jenkins first"
        exit 1
    fi
}

# Check required tools
check_tools() {
    echo ""
    echo -e "${YELLOW}Checking required tools...${NC}"
    
    # Check Java
    if command -v java &> /dev/null; then
        JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
        echo -e "${GREEN}✓ Java installed: $JAVA_VERSION${NC}"
    else
        echo -e "${RED}✗ Java not found${NC}"
        exit 1
    fi
    
    # Check Maven
    if command -v mvn &> /dev/null; then
        MVN_VERSION=$(mvn -version | head -n 1)
        echo -e "${GREEN}✓ Maven installed: $MVN_VERSION${NC}"
    else
        echo -e "${RED}✗ Maven not found${NC}"
        exit 1
    fi
    
    # Check Docker
    if command -v docker &> /dev/null; then
        DOCKER_VERSION=$(docker --version)
        echo -e "${GREEN}✓ Docker installed: $DOCKER_VERSION${NC}"
    else
        echo -e "${YELLOW}⚠ Docker not found (optional)${NC}"
    fi
    
    # Check AWS CLI
    if command -v aws &> /dev/null; then
        AWS_VERSION=$(aws --version)
        echo -e "${GREEN}✓ AWS CLI installed: $AWS_VERSION${NC}"
    else
        echo -e "${YELLOW}⚠ AWS CLI not found (required for deployment)${NC}"
    fi
}

# Create AWS ECR repository
create_ecr_repo() {
    echo ""
    echo -e "${YELLOW}Creating AWS ECR repository...${NC}"
    
    read -p "Enter AWS region (default: us-east-1): " AWS_REGION
    AWS_REGION=${AWS_REGION:-us-east-1}
    
    aws ecr create-repository \
        --repository-name appointment-management \
        --region $AWS_REGION \
        2>/dev/null || echo "Repository may already exist"
    
    echo -e "${GREEN}✓ ECR repository ready${NC}"
}

# Test Jenkins connection
test_jenkins() {
    echo ""
    echo -e "${YELLOW}Testing Jenkins API...${NC}"
    
    JENKINS_URL="http://localhost:8080"
    
    if curl -s "${JENKINS_URL}/api/json" > /dev/null; then
        echo -e "${GREEN}✓ Jenkins API accessible${NC}"
    else
        echo -e "${RED}✗ Cannot access Jenkins API${NC}"
        echo "You may need to configure authentication"
    fi
}

# Display next steps
show_next_steps() {
    echo ""
    echo "=========================================="
    echo -e "${GREEN}Setup Check Complete!${NC}"
    echo "=========================================="
    echo ""
    echo "Next steps:"
    echo "1. Access Jenkins at: http://localhost:8080"
    echo "2. Install required plugins (see JENKINS-SETUP.md)"
    echo "3. Configure AWS credentials"
    echo "4. Create pipeline job"
    echo "5. Run your first build"
    echo ""
    echo "For detailed instructions, see:"
    echo "  - JENKINS-SETUP.md"
    echo "  - README.md"
    echo ""
}

# Main execution
main() {
    check_jenkins
    check_tools
    test_jenkins
    
    echo ""
    read -p "Do you want to create AWS ECR repository? (y/n): " CREATE_ECR
    if [[ $CREATE_ECR == "y" || $CREATE_ECR == "Y" ]]; then
        create_ecr_repo
    fi
    
    show_next_steps
}

# Run main function
main
