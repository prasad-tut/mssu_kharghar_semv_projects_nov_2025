#!/bin/bash
# Frontend Production Build and Test Script
# This script builds the frontend for production and optionally tests it locally

set -e

SKIP_TEST=false
CLEAN=false

# Parse arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        --skip-test)
            SKIP_TEST=true
            shift
            ;;
        --clean)
            CLEAN=true
            shift
            ;;
        *)
            echo "Unknown option: $1"
            echo "Usage: $0 [--skip-test] [--clean]"
            exit 1
            ;;
    esac
done

echo "=== Expense Management System - Frontend Production Build ==="
echo ""

# Navigate to frontend directory
cd "$(dirname "$0")"

# Clean previous build if requested
if [ "$CLEAN" = true ]; then
    echo "Cleaning previous build..."
    if [ -d "dist" ]; then
        rm -rf dist
        echo "✓ Cleaned dist directory"
    fi
    if [ -d "node_modules" ]; then
        rm -rf node_modules
        echo "✓ Cleaned node_modules directory"
    fi
    echo ""
fi

# Install dependencies
echo "Installing dependencies..."
npm install
echo "✓ Dependencies installed"
echo ""

# Build for production
echo "Building for production..."
npm run build
echo "✓ Production build completed"
echo ""

# Display build output
echo "Build output:"
find dist -type f -exec ls -lh {} \; | awk '{print "  " $9 " (" $5 ")"}'
echo ""

# Test locally if not skipped
if [ "$SKIP_TEST" = false ]; then
    echo "Starting preview server for local testing..."
    echo "Press Ctrl+C to stop the server"
    echo ""
    echo "Preview server will be available at: http://localhost:3000/"
    echo "Make sure your backend is running at the configured API URL"
    echo ""
    npm run preview
else
    echo "Skipping local test (use without --skip-test to test)"
    echo ""
    echo "To test the build locally, run:"
    echo "  npm run preview"
fi

echo ""
echo "=== Build Complete ==="
echo "The production build is ready in the 'dist' directory"
echo "See PRODUCTION_DEPLOYMENT.md for deployment instructions"
