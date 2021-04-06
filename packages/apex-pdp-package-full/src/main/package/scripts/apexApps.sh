#!/usr/bin/env sh

#-------------------------------------------------------------------------------
# ============LICENSE_START=======================================================
#  Copyright (C) 2016-2018 Ericsson. All rights reserved.
#  Modifications Copyright (C) 2019-2020 Nordix Foundation.
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
#-------------------------------------------------------------------------------
##
## Script to run APEX Applications, call with '-h' for help
## - adding a new app means to add a command to APEX_APP_MAP and a description to APEX_APP_DESCR_MAP using same/unique key
##
## @package    org.onap.policy.apex
## @author     Sven van der Meer <sven.van.der.meer@ericsson.com>
## @version    v2.0.0
##
##set -x
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
## NOTE: CygWin can not be tested with sh, due to lack of env setup
system=$(uname -s | cut -c1-6)
cpsep=":"
if [ "$system" = "CYGWIN" ] ; then
    APEX_HOME=`cygpath -m ${APEX_HOME}`
    cpsep=";" 
fi
## CP for CP apps
CLASSPATH="$APEX_HOME/etc${cpsep}$APEX_HOME/etc/hazelcast${cpsep}$APEX_HOME/etc/infinispan${cpsep}$APEX_HOME/lib/*"

cmd_list="ws-console ws-echo tpl-event-json model-2-cli cli-editor cli-tosca-editor engine event-generator onappf jmx-test"

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


##
## set java command for each option
##
function set_java_cmd()
{
    case "$1" in
         ws-console)
         {
             echo "java -jar $APEX_HOME/lib/applications/simple-wsclient-$_version-jar-with-dependencies.jar -c"
         };;
         ws-echo)
         {
             echo "java -jar $APEX_HOME/lib/applications/simple-wsclient-$_version-jar-with-dependencies.jar"
         };;
         tpl-event-json)
         {
             echo "java -Dlogback.configurationFile=$APEX_HOME/etc/logback.xml -cp ${CLASSPATH} $_config org.onap.policy.apex.tools.model.generator.model2event.Model2EventMain"
         };;
         model-2-cli)
         {
             echo "java -Dlogback.configurationFile=$APEX_HOME/etc/logback.xml -cp ${CLASSPATH} $_config org.onap.policy.apex.tools.model.generator.model2cli.Model2ClMain"
         };;
         cli-editor)
         {
             echo "java -Dlogback.configurationFile=$APEX_HOME/etc/logback.xml -cp ${CLASSPATH} $_config org.onap.policy.apex.auth.clieditor.ApexCommandLineEditorMain"
         };;
         cli-tosca-editor)
         {
             echo "java -Dlogback.configurationFile=$APEX_HOME/etc/logback.xml -cp ${CLASSPATH} $_config org.onap.policy.apex.auth.clieditor.tosca.ApexCliToscaEditorMain"
         };;
         engine)
         {
             echo "java -Dlogback.configurationFile=$APEX_HOME/etc/logback.xml -cp ${CLASSPATH} $_config org.onap.policy.apex.service.engine.main.ApexMain"
         };;
         event-generator)
         {
             echo "java -Dlogback.configurationFile=$APEX_HOME/etc/logback.xml -cp ${CLASSPATH} $_config org.onap.policy.apex.testsuites.performance.benchmark.eventgenerator.EventGenerator"
         };;
         onappf)
         {
             echo "java -Dlogback.configurationFile=$APEX_HOME/etc/logback.xml -cp ${CLASSPATH} $_config org.onap.policy.apex.services.onappf.ApexStarterMain"
         };;
         jmx-test)
         {
             echo "java -Dlogback.configurationFile=$APEX_HOME/etc/logback.xml -cp ${CLASSPATH} $_config $_jmxconfig org.onap.policy.apex.service.engine.main.ApexMain"
         };;
         *)
         {
             echo ""
         };;
    esac
}


##
## print the description for each option
##
function print_description()
{
    case "$1" in
         ws-console)
         {
             echo "a simple console sending events to APEX, connect to APEX consumer port"
             echo ""
         };;
         ws-echo)
         {
             echo "a simple echo client printing events received from APEX, connect to APEX producer port"
             echo ""
         };;
         tpl-event-json)
         {
             echo "provides JSON templates for events generated from a policy model"
             echo ""
         };;
         model-2-cli)
         {
             echo "generates CLI Editor Commands from a policy model"
             echo ""
         };;
         cli-editor)
         {
             echo "runs the APEX CLI Editor"
             echo ""
         };;
         cli-tosca-editor)
         {
             echo "runs the APEX CLI Tosca Editor"
             echo ""
         };;
         engine)
         {
             echo "starts the APEX engine"
             echo ""
         };;
         event-generator)
         {
             echo "starts the event generator in a simple webserver for performance testing"
             echo ""
         };;
         onappf)
         {
             echo "starts the ApexStarter which handles the Apex Engine based on instructions from PAP"
             echo ""
         };;
         jmx-test)
         {
             echo "starts the APEX engine with creating jmx connection configuration"
             echo ""
         };;
         *)
         {
             echo "$MOD_SCRIPT_NAME: unknown application '$1'"
             echo "$MOD_SCRIPT_NAME: supported applications:"
             echo " --> ${cmd_list}"
             echo ""
         };;
    esac
}

if [ $# -eq 0 ]; then
    Help
fi


##
## read command line, cannot do as while here due to 2-view CLI
##
if [ "$1" = "-l" ]; then
    echo "$MOD_SCRIPT_NAME: supported applications:"
    echo " --> ${cmd_list}"
    echo ""
    exit 0
fi
if [ "$1" = "-d" ]; then
    if [ -z "$2" ]; then
        echo "$MOD_SCRIPT_NAME: no application given to describe, supported applications:"
        echo " --> ${cmd_list}"
        echo ""
    else
        print_description $2
    fi
    exit 0;
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
_cmd=$(set_java_cmd $_app)
if [ -z "${_cmd}" ]; then
    echo "$MOD_SCRIPT_NAME: unknown application '$_app'"
    echo "$MOD_SCRIPT_NAME: supported applications:"
    echo " --> ${cmd_list}"
    echo ""
    exit 0
fi
_cmd="$_cmd $*"
echo "$MOD_SCRIPT_NAME: running application '$_app' with command '$_cmd'"
exec $_cmd

