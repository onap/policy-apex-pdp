#!/bin/bash
REGISTRY='onapmulti'
IMAGE_NAME=$1 
IMAGE_TAG=$2
#create the template to be used with the manifest list
cat > ${IMAGE_NAME}_${IMAGE_TAG}.yaml <<EOF
image: ${REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG}
manifests:
  -
    image: ${REGISTRY}/${IMAGE_NAME}:amd64
    platform:
      architecture: amd64
      os: linux
  -
    image: ${REGISTRY}/${IMAGE_NAME}:aarch64
    platform:
      architecture: arm64
      os: linux
EOF
