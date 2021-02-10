#!/bin/bash
# ============LICENSE_START=======================================================
#  Copyright (C) 2018 Ericsson. All rights reserved.
#
#  Modifications copyright (c) 2019 Nordix Foundation.
#  Modifications Copyright (C) 2020-2021 AT&T Intellectual Property.
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
source ${SCRIPTS}/get-branch-mariadb.sh

echo "Uninstall docker-py and reinstall docker."
pip uninstall -y docker-py
pip uninstall -y docker
pip install -U docker==2.7.0

sudo apt-get -y install libxml2-utils

source ${SCRIPTS}/detmVers.sh

docker-compose -f ${SCRIPTS}/docker-compose-all.yml up -d apex-pdp

unset http_proxy https_proxy

POLICY_API_IP=`get-instance-ip.sh policy-api`
POLICY_PAP_IP=`get-instance-ip.sh policy-pap`
MARIADB_IP=`get-instance-ip.sh mariadb`
APEX_IP=`get-instance-ip.sh policy-apex-pdp`
DMAAP_IP=`get-instance-ip.sh policy.api.simpledemo.onap.org`

echo PAP IP IS ${POLICY_PAP_IP}
echo MARIADB IP IS ${MARIADB_IP}
echo API IP IS ${POLICY_API_IP}
echo APEX IP IS ${APEX_IP}
echo DMAAP_IP IS ${DMAAP_IP}

# wait for the app to start up
${SCRIPTS}/wait_for_port.sh ${APEX_IP} 6969

ROBOT_VARIABLES=""
ROBOT_VARIABLES="${ROBOT_VARIABLES} -v APEX_IP:${APEX_IP}"
ROBOT_VARIABLES="${ROBOT_VARIABLES} -v POLICY_API_IP:${POLICY_API_IP}"
ROBOT_VARIABLES="${ROBOT_VARIABLES} -v POLICY_PAP_IP:${POLICY_PAP_IP}"
