#-------------------------------------------------------------------------------
# ============LICENSE_START=======================================================
#  Copyright (C) 2022 Nordix Foundation.
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

FROM busybox AS tarball
RUN mkdir /packages /extracted
COPY /maven/apex-pdp-package-full.tar.gz /packages/
RUN tar xvzf /packages/apex-pdp-package-full.tar.gz --directory /extracted/

FROM opensuse/leap:15.4

LABEL maintainer="Policy Team"
LABEL org.opencontainers.image.title="Policy APEX PDP"
LABEL org.opencontainers.image.description="Policy APEX PDP image based on OpenSuse"
LABEL org.opencontainers.image.url="https://github.com/onap/policy-apex-pdp"
LABEL org.opencontainers.image.vendor="ONAP Policy Team"
LABEL org.opencontainers.image.licenses="Apache-2.0"
LABEL org.opencontainers.image.created="${git.build.time}"
LABEL org.opencontainers.image.version="${git.build.version}"
LABEL org.opencontainers.image.revision="${git.commit.id.abbrev}"

ARG POLICY_LOGS=/var/log/onap/policy/apex-pdp
ENV POLICY_HOME=/opt/app/policy/apex-pdp
ENV POLICY_LOGS=$POLICY_LOGS
ENV LANG=en_US.UTF-8 LANGUAGE=en_US:en LC_ALL=en_US.UTF-8
ENV JAVA_HOME=/usr/lib64/jvm/java-11-openjdk-11

RUN zypper -n -q install --no-recommends java-11-openjdk-devel netcat-openbsd \
    && zypper -n -q update && zypper -n -q clean --all \
    && groupadd --system apexuser && useradd --system --shell /bin/sh -G apexuser apexuser \
    && mkdir -p $POLICY_HOME $POLICY_LOGS \
    && chown -R apexuser:apexuser $POLICY_HOME $POLICY_LOGS

COPY --chown=apexuser:apexuser --from=tarball /extracted $POLICY_HOME
RUN cp -pr $POLICY_HOME/examples /home/apexuser

USER apexuser
ENV PATH $POLICY_HOME/bin:$PATH
WORKDIR /home/apexuser
ENTRYPOINT [ "/bin/sh" ]
