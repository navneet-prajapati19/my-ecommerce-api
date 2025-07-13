#!/bin/bash

kubectl apply -f 03-secrets/app-secrets.yaml

kubectl apply -f postgres/configmap.yaml
kubectl apply -f postgres/pv.yaml
kubectl apply -f postgres/pvc.yaml
kubectl apply -f postgres/deployment.yaml
kubectl apply -f postgres/service.yaml
kubectl apply -f postgres/pgadmin.yaml


echo "⏳ Waiting for PostgreSQL pod to be ready..."
kubectl wait --for=condition=ready pod -l app=postgresql --timeout=120s

kubectl apply -f keycloak/init-keycloak-db-job.yaml
kubectl apply -f keycloak/configmap.yaml
kubectl apply -f keycloak/deployment.yaml
kubectl apply -f keycloak/service.yaml

kubectl apply -f redis/deployment.yaml
kubectl apply -f redis/service.yaml


#kubectl apply -f spring-boot/deployment.yaml
#kubectl apply -f spring-boot/service.yaml

kubectl apply -f ingress/ingress.yaml
