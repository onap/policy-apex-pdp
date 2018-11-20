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

$APEX_HOME/bin/runOneBenchmark.sh Javascript 01
$APEX_HOME/bin/runOneBenchmark.sh Javascript 02
$APEX_HOME/bin/runOneBenchmark.sh Javascript 04
$APEX_HOME/bin/runOneBenchmark.sh Javascript 08
$APEX_HOME/bin/runOneBenchmark.sh Javascript 16
$APEX_HOME/bin/runOneBenchmark.sh Javascript 32
$APEX_HOME/bin/runOneBenchmark.sh Javascript 64

$APEX_HOME/bin/runOneBenchmark.sh Jython 01
$APEX_HOME/bin/runOneBenchmark.sh Jython 02
$APEX_HOME/bin/runOneBenchmark.sh Jython 04
$APEX_HOME/bin/runOneBenchmark.sh Jython 08
$APEX_HOME/bin/runOneBenchmark.sh Jython 16
$APEX_HOME/bin/runOneBenchmark.sh Jython 32
$APEX_HOME/bin/runOneBenchmark.sh Jython 64

$APEX_HOME/bin/runOneBenchmark.sh Mvel 01
$APEX_HOME/bin/runOneBenchmark.sh Mvel 02
$APEX_HOME/bin/runOneBenchmark.sh Mvel 04
$APEX_HOME/bin/runOneBenchmark.sh Mvel 08
$APEX_HOME/bin/runOneBenchmark.sh Mvel 16
$APEX_HOME/bin/runOneBenchmark.sh Mvel 32
$APEX_HOME/bin/runOneBenchmark.sh Mvel 64

$APEX_HOME/bin/runOneBenchmark.sh Java 01
$APEX_HOME/bin/runOneBenchmark.sh Java 02
$APEX_HOME/bin/runOneBenchmark.sh Java 04
$APEX_HOME/bin/runOneBenchmark.sh Java 08
$APEX_HOME/bin/runOneBenchmark.sh Java 16
$APEX_HOME/bin/runOneBenchmark.sh Java 32
$APEX_HOME/bin/runOneBenchmark.sh Java 64

#$APEX_HOME/bin/runOneBenchmark.sh JRuby 01
#$APEX_HOME/bin/runOneBenchmark.sh JRuby 02
#$APEX_HOME/bin/runOneBenchmark.sh JRuby 04
#$APEX_HOME/bin/runOneBenchmark.sh JRuby 08
#$APEX_HOME/bin/runOneBenchmark.sh JRuby 16
#$APEX_HOME/bin/runOneBenchmark.sh JRuby 32
#$APEX_HOME/bin/runOneBenchmark.sh JRuby 64
