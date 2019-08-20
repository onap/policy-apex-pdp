#!/bin/bash
source version.properties
TAGS=("${release_version}" "${release_version}-$(date -u +"%Y%m%d%H%M%S")" "${snapshot_version}-latest")
for TAG in ${TAGS[@]}
do
./docker_manifest.sh policy-base-alpine $TAG
./docker_manifest.sh policy-common-alpine $TAG
done
HOST_ARCH='amd64'
if [ "$(uname -m)" == 'aarch64' ]
then
    HOST_ARCH='arm64'
fi
MT_RELEASE='v0.9.0'
wget https://github.com/estesp/manifest-tool/releases/download/${MT_RELEASE}/manifest-tool-linux-${HOST_ARCH} -O ./manifest-tool
chmod u+x manifest-tool
for TAG in ${TAGS[@]}
do
./manifest-tool push from-spec policy-base-alpine_$TAG.yaml
./manifest-tool push from-spec policy-common-alpine_$TAG.yaml
done
