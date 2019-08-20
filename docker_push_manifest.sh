#!/bin/bash -ex
#   ============LICENSE_START=======================================================
#    Copyright (C) 2019 ENEA AB. All rights reserved.
#   ================================================================================
#   Licensed under the Apache License, Version 2.0 (the "License");
#   you may not use this file except in compliance with the License.
#   You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
#   Unless required by applicable law or agreed to in writing, software
#   distributed under the License is distributed on an "AS IS" BASIS,
#   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#   See the License for the specific language governing permissions and
#   limitations under the License.
#
#   SPDX-License-Identifier: Apache-2.0
#   ============LICENSE_END=========================================================

# This script creates the multi-arch manifest for the docker images

# shellcheck source=/dev/null
source version.properties
IMAGES="cristinapauna/policy-base-alpine cristinapauna/policy-common-alpine"
ARCHES="amd64 arm64"
TIMESTAMP=$(date -u +"%Y%m%d%H%M%S")
MT_RELEASE='v0.9.0'

# Download the manifest tool based on the host's architecture
HOST_ARCH='amd64'
if [ "$(uname -m)" == 'aarch64' ]; then
    HOST_ARCH='arm64'
fi
wget https://github.com/estesp/manifest-tool/releases/download/${MT_RELEASE}/manifest-tool-linux-${HOST_ARCH} -O ./manifest-tool
chmod u+x manifest-tool

# Tag the images and push the manifest (do not fail if some prerequisite tags are not yet present)
set +e
for image in ${IMAGES}; do
    # always (re)create both SNAPSHOT and STAGING tags to make sure everything is up to date
    TAGS="latest ${release_version} ${release_version}-SNAPSHOT ${release_version}-SNAPSHOT-latest ${release_version}-STAGING-latest"
    for tag in ${TAGS}; do
        ./manifest-tool push from-args \
            --ignore-missing \
            --platforms "linux/${ARCHES// /,linux/}" \
            --template "${image}:${tag}-ARCH" \
            --target "${image}:${tag}"
    done

    # Create timestamped multiarch tag; if the script is ran from the merge
    # job then add the SNAPSHOT suffix
    [[ "${PARENT_JOB_NAME}" =~ merge ]] && snapshot_suffix="SNAPSHOT-"

    ./manifest-tool push from-args \
        --ignore-missing \
        --platforms "linux/${ARCHES// /,linux/}" \
        --template "${image}:${release_version}-${snapshot_suffix:-}ARCH" \
        --target "${image}:${release_version}-${snapshot_suffix:-}${TIMESTAMP}"
done
