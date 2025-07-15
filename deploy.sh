#!/bin/bash

# Variables
APP_NAME="spring-boot-app"
IMAGE_NAME="ion"

# Generate unique tag (timestamp-based)
TAG="v$(date +%s)"
FULL_IMAGE="$IMAGE_NAME:$TAG"

echo "🚀 Building Docker image: $FULL_IMAGE"
docker build -t $FULL_IMAGE .

if [ $? -ne 0 ]; then
    echo "❌ Docker build failed. Exiting."
    exit 1
fi

echo "📦 Pushing image to local Docker registry (Docker Desktop uses local images directly)"
# No need to push or load image when using Docker Desktop’s built-in K8s

echo "🔁 Updating Kubernetes deployment..."
kubectl set image deployment/$APP_NAME $APP_NAME=$FULL_IMAGE

echo "⏳ Waiting for rollout to complete..."
if ! kubectl rollout status deployment/$APP_NAME; then
    echo "❌ Rollout failed or timed out."
    exit 1
fi

echo "✅ Deployment updated successfully with image: $FULL_IMAGE"
kubectl rollout status deployment/$APP_NAME

echo "📜 Tailing logs..."
LATEST_POD=$(kubectl get pods -l app=$APP_NAME -o jsonpath="{.items[0].metadata.name}")
#kubectl logs -f $LATEST_POD
