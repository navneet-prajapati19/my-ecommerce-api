#!/bin/bash

kubectl apply -f postgres/pv.yaml
kubectl apply -f postgres/pvc.yaml
kubectl apply -f postgres/deployment.yaml
kubectl apply -f postgres/service.yaml
kubectl apply -f postgres/configmap.yaml
kubectl apply -f redis/deployment.yaml
kubectl apply -f redis/service.yaml
kubectl apply -f keycloak/configmap.yaml
kubectl apply -f keycloak/deployment.yaml
kubectl apply -f keycloak/service.yaml
kubectl apply -f spring-boot/deployment.yaml
kubectl apply -f spring-boot/service.yaml
kubectl apply -f ingress/nginx-ingress-controller.yaml
kubectl apply -f ingress/ingress.yaml
kubectl apply -f 03-secrets/app-secrets.yaml