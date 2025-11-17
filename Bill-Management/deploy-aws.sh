#!/bin/bash

# AWS Deployment Script for Bill Management System

set -e

# Load environment variables from .env file
if [ -f .env ]; then
    echo "Loading environment variables from .env file..."
    export $(cat .env | grep -v '^#' | xargs)
else
    echo "Warning: .env file not found. Using default values."
fi

# Configuration
AWS_REGION="${AWS_REGION:-us-east-1}"
ECR_REPO_NAME="${ECR_REPO_NAME:-bill-management}"
ECS_CLUSTER="${ECS_CLUSTER:-bill-management-cluster}"
ECS_SERVICE="${ECS_SERVICE:-bill-management-service}"
S3_BUCKET="${S3_BUCKET:-bill-management-frontend}"
AWS_ACCOUNT_ID="${AWS_ACCOUNT_ID:-$(aws sts get-caller-identity --query Account --output text)}"
IMAGE_TAG="${IMAGE_TAG:-latest}"

echo "=========================================="
echo "🚀 Bill Management System - AWS Deployment"
echo "=========================================="
echo "Region: ${AWS_REGION}"
echo "Account: ${AWS_ACCOUNT_ID}"
echo "Image Tag: ${IMAGE_TAG}"
echo "=========================================="

# 1. Build Docker Image
echo ""
echo "📦 Step 1: Building Docker image..."
cd bill_management
docker build -t ${ECR_REPO_NAME}:${IMAGE_TAG} .
cd ..
echo "✅ Docker image built successfully"

# 2. Login to ECR
echo ""
echo "🔐 Step 2: Logging into ECR..."
aws ecr get-login-password --region ${AWS_REGION} | \
    docker login --username AWS --password-stdin \
    ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com
echo "✅ Logged into ECR"

# 3. Tag and Push Image
echo ""
echo "⬆️  Step 3: Pushing image to ECR..."
docker tag ${ECR_REPO_NAME}:${IMAGE_TAG} \
    ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPO_NAME}:${IMAGE_TAG}
docker push ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPO_NAME}:${IMAGE_TAG}
echo "✅ Image pushed to ECR"

# 4. Update ECS Service
echo ""
echo "🔄 Step 4: Updating ECS service..."
aws ecs update-service \
    --cluster ${ECS_CLUSTER} \
    --service ${ECS_SERVICE} \
    --force-new-deployment \
    --region ${AWS_REGION}
echo "✅ ECS service updated"

# 5. Deploy Frontend to S3
echo ""
echo "🌐 Step 5: Deploying frontend to S3..."
# Update API URL in frontend for production
sed -i.bak "s|http://localhost:7890|/api|g" frontend/script.js
aws s3 sync frontend/ s3://${S3_BUCKET} --delete --acl public-read
# Restore original file
mv frontend/script.js.bak frontend/script.js
echo "✅ Frontend deployed to S3"

# 6. Invalidate CloudFront cache (if exists)
echo ""
echo "🔄 Step 6: Checking for CloudFront distribution..."
DISTRIBUTION_ID=$(aws cloudfront list-distributions \
    --query "DistributionList.Items[?Origins.Items[?DomainName=='${S3_BUCKET}.s3.amazonaws.com']].Id" \
    --output text 2>/dev/null || echo "")

if [ -n "$DISTRIBUTION_ID" ]; then
    echo "Found CloudFront distribution: ${DISTRIBUTION_ID}"
    echo "Invalidating cache..."
    aws cloudfront create-invalidation \
        --distribution-id ${DISTRIBUTION_ID} \
        --paths "/*"
    echo "✅ CloudFront cache invalidated"
else
    echo "ℹ️  No CloudFront distribution found, skipping cache invalidation"
fi

echo ""
echo "=========================================="
echo "✅ Deployment completed successfully!"
echo "=========================================="
echo "Backend: Check ECS service status"
echo "Frontend: https://${S3_BUCKET}.s3-website-${AWS_REGION}.amazonaws.com"
echo "=========================================="
