#!/bin/bash

set -e

kind create cluster --config=single-node.yaml

kubectl config set-context --current --namespace default

# helm upgrade metrics-server --install \
# --set apiService.create=true \
# --set extraArgs.kubelet-insecure-tls=true \
# --set extraArgs.kubelet-preferred-address-types=InternalIP \
# bitnami/metrics-server --namespace kube-system


# 部署istio
istioctl operator init
kubectl apply -f install-istio.yaml
kubectl label namespace default istio-injection=enabled


kubectl apply -f ../k8s


# https://istio.io/latest/docs/ops/integrations/kiali/

kubectl apply -f https://raw.githubusercontent.com/istio/istio/release-1.13/samples/addons/kiali.yaml
kubectl patch service kiali -n istio-system -p '{"spec":{"type":"NodePort"}}'
kubectl patch service kiali -n istio-system --type=json -p='[{"op": "replace", "path": "/spec/ports/0/nodePort", "value": 30085}]'

