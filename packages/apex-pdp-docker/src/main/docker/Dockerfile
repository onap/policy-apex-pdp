#-------------------------------------------------------------------------------
# ============LICENSE_START=======================================================
#  Copyright (C) 2020 Nordix Foundation.
#  Modification Copyright (C) 2020-2021 AT&T Foundation.
#  Modifications Copyright (C) 2021 Bell Canada.
# ================================================================================
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# SPDX-License-Identifier: Apache-2.0
# ============LICENSE_END=========================================================
#-------------------------------------------------------------------------------

#
# Docker file to build an image that runs APEX on Java 11 or better in alpine
#
FROM onap/policy-jdk-alpine:2.2.1

LABEL maintainer="Policy Team"

ARG POLICY_LOGS=/var/log/onap/policy/apex-pdp
ENV POLICY_HOME=/opt/app/policy/apex-pdp
ENV POLICY_LOGS=$POLICY_LOGS

RUN apk update \
    && apk add --no-cache \
        vim \
        iputils \
    && addgroup -S apexuser && adduser -S apexuser -G apexuser \
    && mkdir -p $POLICY_HOME \
    && mkdir -p $POLICY_LOGS \
    && chown -R apexuser:apexuser $POLICY_LOGS \
    && mkdir /packages

COPY /maven/apex-pdp-package-full.tar.gz /packages
RUN tar xvfz /packages/apex-pdp-package-full.tar.gz --directory $POLICY_HOME \
    && rm /packages/apex-pdp-package-full.tar.gz \
    && find /opt/app -type d -perm 755 \
    && find /opt/app -type f -perm 644 \
    && chmod 755 $POLICY_HOME/bin/* \
    && cp -pr $POLICY_HOME/examples /home/apexuser \
    && chown -R apexuser:apexuser /home/apexuser/* $POLICY_HOME \
    && chmod 755 $POLICY_HOME/etc/*

USER apexuser
ENV PATH $POLICY_HOME/bin:$PATH
WORKDIR /home/apexuser
ENTRYPOINT [ "/bin/sh" ]
