#!/usr/bin/env bash

#-------------------------------------------------------------------------------
# ============LICENSE_START=======================================================
#  Copyright (C) 2016-2018 Ericsson. All rights reserved.
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
## Script to run the APEX
## - requires BASH with associative arrays, bash of at least version 4
## - for BASH examples with arrays see for instance: http://www.artificialworlds.net/blog/2012/10/17/bash-associative-array-examples/
## - adding a new app means to add a command to APEX_APP_MAP and a description to APEX_APP_DESCR_MAP using same/unique key
##
## @package    com.ericsson.apex.apex
## @author     Sven van der Meer <sven.van.der.meer@ericsson.com>
## @version    v0.7.0

##
## DO NOT CHANGE CODE BELOW, unless you know what you are doing
##

if [ -z $APEX_USER ]
then
	APEX_USER="apexuser"
fi

id $APEX_USER > /dev/null 2>& 1
if [ "$?" -ne "0" ]
then
	echo 'cannot run apex, user "'$APEX_USER'" does not exit'
	exit
fi

if [ $(whoami) != "$APEX_USER" ]
then
	echo 'Apex must be run as user "'$APEX_USER'"'
	exit
fi

if [ -z $APEX_HOME ]
then
	APEX_HOME="/opt/ericsson/apex/apex"
fi

if [ ! -d $APEX_HOME ]
then
	echo
	echo 'Apex directory "'$APEX_HOME'" not set or not a directory'
	echo "Please set environment for 'APEX_HOME'"
	exit
fi

if [ $(whoami) == "$APEX_USER" ]
then
	$APEX_HOME/bin/apexApps.sh engine $*
else
	su $APEX_USER -c "$APEX_HOME/bin/apexApps.sh engine $*"
fi
