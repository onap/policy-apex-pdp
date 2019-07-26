#!/usr/bin/env bash

#-------------------------------------------------------------------------------
# ============LICENSE_START=======================================================
#  Copyright (C) 2019 Nordix Foundation.
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

##
## Script to run the APEX CLI Tosca Editor, calls apexApps.sh
##
## @package    org.onap.policy.apex
## @author     Ajith Sreekumar <ajith.sreekumar@est.tech>
## @version    v1.0.0

if [ -z $APEX_HOME ]
then
    APEX_HOME="/opt/app/policy/apex-pdp"
fi

if [ ! -d $APEX_HOME ]
then
    echo
    echo 'Apex directory "'$APEX_HOME'" not set or not a directory'
    echo "Please set environment for 'APEX_HOME'"
    exit
fi

$APEX_HOME/bin/apexApps.sh cli-tosca-editor $*
