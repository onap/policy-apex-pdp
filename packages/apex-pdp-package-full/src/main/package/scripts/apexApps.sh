#!/usr/bin/env ash

#-------------------------------------------------------------------------------
# ============LICENSE_START=======================================================
#  Copyright (C) 2016-2018 Ericsson. All rights reserved.
#  Modifications Copyright (C) 2019-2020 Nordix Foundation.
#  Modifications Copyright (C) 2020 AT&T Intellectual Property.
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
## Script to run APEX Applications, call with '-h' for help
## - requires BASH with associative arrays, bash of at least version 4
## - for BASH examples with arrays see for instance: http://www.artificialworlds.net/blog/2012/10/17/bash-associative-array-examples/
## - adding a new app means to add a command to APEX_APP_MAP and a description to APEX_APP_DESCR_MAP using same/unique key
##
## @package    org.onap.policy.apex
## @author     Sven van der Meer <sven.van.der.meer@ericsson.com>
## @version    v2.0.0
##
## convert to ash shell script 12/1/2020
##

##
## DO NOT CHANGE CODE BELOW, unless you know what you are doing
##

if [ -z "$APEX_HOME" ]; then
    APEX_HOME="/opt/app/policy/apex-pdp"
fi

if [ ! -d "$APEX_HOME" ]; then
    echo
    echo 'Apex directory "'$APEX_HOME'" not set or not a directory'
    echo "Please set environment for 'APEX_HOME'"
    exit
fi

## Environment variables for HTTPS
KEYSTORE="${KEYSTORE:-$APEX_HOME/etc/ssl/policy-keystore}"
TRUSTSTORE="${TRUSTSTORE:-$APEX_HOME/etc/ssl/policy-truststore}"
KEYSTORE_PASSWORD="${KEYSTORE_PASSWORD:-Pol1cy_0nap}"
TRUSTSTORE_PASSWORD="${TRUSTSTORE_PASSWORD:-Pol1cy_0nap}"

## HTTPS parameters
HTTPS_PARAMETERS="-Djavax.net.ssl.keyStore=${KEYSTORE} -Djavax.net.ssl.keyStorePassword=${KEYSTORE_PASSWORD} -Djavax.net.ssl.trustStore=$TRUSTSTORE -Djavax.net.ssl.trustStorePassword=${TRUSTSTORE_PASSWORD}"

## script name for output
MOD_SCRIPT_NAME=$(basename $0)


## config for CP apps
_config="${HTTPS_PARAMETERS} -Dlogback.configurationFile=$APEX_HOME/etc/logback.xml -Dhazelcast.config=$APEX_HOME/etc/hazelcast.xml -Dhazelcast.mancenter.enabled=false"

## jmx test config
_jmxconfig="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9911 -Dcom.sun.management.jmxremote.local.only=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false "

## Maven/APEX version
_version=$(cat $APEX_HOME/etc/app-version.txt)

## system to get CygWin paths                                                     
## NOTE: CygWin can not be tested with ash, due to lack of env setup
system=$(uname -s | cut -c1-6)
cpsep=":"
if [ "$system" = "CYGWIN" ] ; then
    APEX_HOME=`cygpath -m ${APEX_HOME}`
    cpsep=";" 
fi
## CP for CP apps
CLASSPATH="$APEX_HOME/etc${cpsep}$APEX_HOME/etc/hazelcast${cpsep}$APEX_HOME/etc/infinispan${cpsep}$APEX_HOME/lib/*"

##
## use files to mimic hashing array function in bash
##
function genFile
{
local dir=$1
local file=$2
local content=$3
cat <<EOF > ${dir}/${file}
${content}
EOF
}
##
## Help screen and exit condition (i.e. too few arguments)
##
function Help()
{
    echo ""
    echo "$MOD_SCRIPT_NAME - runs APEX applications"
    echo ""
    echo "       Usage:  $MOD_SCRIPT_NAME [options] | [<application> [<application options>]]"
    echo ""
    echo "       Options"
    echo "         -d <app>    - describes an application"
    echo "         -l          - lists all applications supported by this script"
    echo "         -h          - this help screen"
    echo ""
    echo ""
    exit 255
}

###
## array of applications with name=command
## use tmpdir/app/key file structure to mimic hashing array in bash
app_map_dir=$(mktemp -d)

genFile ${app_map_dir} "ws-console" "java -jar $APEX_HOME/lib/applications/simple-wsclient-$_version-jar-with-dependencies.jar -c"
genFile ${app_map_dir} "ws-echo" "java -jar $APEX_HOME/lib/applications/simple-wsclient-$_version-jar-with-dependencies.jar"
genFile ${app_map_dir} "tpl-event-json" "java -Dlogback.configurationFile=$APEX_HOME/etc/logback.xml -cp ${CLASSPATH} $_config org.onap.policy.apex.tools.model.generator.model2event.Model2EventMain"
genFile ${app_map_dir} "model-2-cli" "java -Dlogback.configurationFile=$APEX_HOME/etc/logback.xml -cp ${CLASSPATH} $_config org.onap.policy.apex.tools.model.generator.model2cli.Model2ClMain"
genFile ${app_map_dir} "cli-editor" "java -Dlogback.configurationFile=$APEX_HOME/etc/logback.xml -cp ${CLASSPATH} $_config org.onap.policy.apex.auth.clieditor.ApexCommandLineEditorMain"
genFile ${app_map_dir} "cli-tosca-editor" "java -Dlogback.configurationFile=$APEX_HOME/etc/logback.xml -cp ${CLASSPATH} $_config org.onap.policy.apex.auth.clieditor.tosca.ApexCliToscaEditorMain"
genFile ${app_map_dir} "engine" "java -Dlogback.configurationFile=$APEX_HOME/etc/logback.xml -cp ${CLASSPATH} $_config org.onap.policy.apex.service.engine.main.ApexMain"
genFile ${app_map_dir} "event-gen" "java -Dlogback.configurationFile=$APEX_HOME/etc/logback.xml -cp ${CLASSPATH} $_config org.onap.policy.apex.testsuites.performance.benchmark.eventgenerator.EventGenerator"
genFile ${app_map_dir} "onappf" "java -Dlogback.configurationFile=$APEX_HOME/etc/logback.xml -cp ${CLASSPATH} $_config org.onap.policy.apex.services.onappf.ApexStarterMain"
genFile ${app_map_dir} "jmx-test" "java -Dlogback.configurationFile=$APEX_HOME/etc/logback.xml -cp ${CLASSPATH} $_config $_jmxconfig org.onap.policy.apex.service.engine.main.ApexMain"

###
## array of applications with name=description
## use tmpdir/app/key file structure to mimic hashing array in bash
app_descr_dir=$(mktemp -d)

genFile ${app_descr_dir} "ws-console" "a simple console sending events to APEX, connect to APEX consumer port"
genFile ${app_descr_dir} "ws-echo" "a simple echo client printing events received from APEX, connect to APEX producer port"
genFile ${app_descr_dir} "tpl-event-json" "provides JSON templates for events generated from a policy model"
genFile ${app_descr_dir} "model-2-cli" "generates CLI Editor Commands from a policy model"
genFile ${app_descr_dir} "cli-editor" "runs the APEX CLI Editor"
genFile ${app_descr_dir} "cli-tosca-editor" "runs the APEX CLI Tosca Editor"
genFile ${app_descr_dir} "engine" "starts the APEX engine"
genFile ${app_descr_dir} "event-generator" "starts the event generator in a simple webserver for performance testing"
genFile ${app_descr_dir} "onappf" "starts the ApexStarter which handles the Apex Engine based on instructions from PAP"
genFile ${app_descr_dir} "jmx-test" "starts the APEX engine with creating jmx connection configuration"

trap "rm -rf ${app_map_dir} ${app_descr_dir}" 0 1 2 3 15

if [ $# -eq 0 ]; then
    Help
fi

##
## read command line, cannot do as while here due to 2-view CLI
##
if [ "$1" = "-l" ]; then
    echo "$MOD_SCRIPT_NAME: supported applications:"
    list=$(ls -1 ${app_descr_dir} | sed ':a;N;$!ba;s/\n/ /g' )
    echo " --> ${list}"
    echo ""
    exit 0
fi
if [ "$1" = "-d" ]; then
    if [ -z "$2" ]; then
        echo "$MOD_SCRIPT_NAME: no application given to describe, supported applications:"
        list=$(ls -1 ${app_descr_dir} | sed ':a;N;$!ba;s/\n/ /g' )
        echo " --> ${list}"
        echo ""
        exit 0;
    else
        if [ ! -f "${app_descr_dir}/$2" ]; then
            echo "$MOD_SCRIPT_NAME: unknown application '$2'"
            echo ""
            exit 0;
        fi
        _cmd=$(cat ${app_descr_dir}/$2)
        echo "$MOD_SCRIPT_NAME: application '$2'"
        echo " --> $_cmd"
        echo ""
        exit 0;
    fi
fi
if [ "$1" = "-h" ]; then
    Help
    exit 0
fi

#
# begin to run java
#
_app=$1
shift
if [ ! -f "${app_map_dir}/${_app}" ]; then
    echo "$MOD_SCRIPT_NAME: application '$_app' not supported"
    exit 1
fi
_cmd=$(cat ${app_map_dir}/${_app})
_cmd="$_cmd $*"
echo $_cmd
## echo "$MOD_SCRIPT_NAME: running application '$_app' with command '$_cmd'"
exec $_cmd

