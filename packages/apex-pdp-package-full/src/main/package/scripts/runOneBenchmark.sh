#!/usr/bin/env bash

#-------------------------------------------------------------------------------
# ============LICENSE_START=======================================================
#  Copyright (C) 2018 Ericsson. All rights reserved.
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

if [ $# -ne 2 ]
then
    echo "usage: $0 executor-type thread-count"
    echo "  executor-type [Javascript|Jython|JRuby|Mvel|Java]"
    echo "  thread-count [01|02|04|08|16|32|64]"
    exit 1
fi

if [ "$1" != "Javascript" ] && [ "$1" != "Jython" ] && [ "$1" != "JRuby" ] && [ "$1" != "Mvel" ] && [ "$1" != "Java" ]
then
   echo "executor-type must be a member of the set [Javascript|Jython|JRuby|Mvel|Java]"
   exit 1
fi
    
if [ "$2" != "01" ] && [ "$2" != "02" ] && [ "$2" != "04" ] && [ "$2" != "08" ] && [ "$2" != "16" ] && [ "$2" != "32" ] && [ "$2" != "64" ]
then
    echo "thread-count must be a member of the set [01|02|04|08|16|32|64]"
   exit 1
fi
    
# Remove the old benchmark test result file if it exists
rm -fr examples/benchmark/Bm$1$2.json

# Start the event generator
/bin/bash bin/apexApps.sh event-gen -c examples/benchmark/EventGeneratorConfig.json -o examples/benchmark/Bm$1$2.json > examples/benchmark/Bm$1$2_gen.log 2>&1 &

# Start Apex
sleep 2
/bin/bash bin/apexApps.sh engine -c examples/benchmark/$1$2.json > examples/benchmark/Bm$1$2_apex.log 2>&1 &
apex_pid=`ps -A -o pid,cmd | grep ApexMain | grep -v grep | head -n 1 | awk '{print $1}'`

echo "running benchmark test for executor $1 with $2 threads" 

# Loop until result file exists
while [ ! -f examples/benchmark/Bm$1$2.json ]
do
    echo -n .
    sleep 1
done

# Loop until result file has 318 lines
while [ "$(wc -l examples/benchmark/Bm$1$2.json | cut -f1 -d' ')" -lt 318 ]
do
    echo -n .
    sleep 1
done

# Kill Apex
kill $apex_pid

sleep 5

echo ""
echo "Finished"
