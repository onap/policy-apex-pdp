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
## Script to copy css artifacts from parent to all child modules, recursively.
##
## @author     Sven van der Meer <sven.van.der.meer@ericsson.com>
## @version    v2.0.0


##
## DO NOT CHANGE CODE BELOW, unless you know what you are doing
##

## script name for output
MOD_SCRIPT_NAME=`basename $0`

echo 
echo "$MOD_SCRIPT_NAME: copying standard css and images to modules"
for dir in `find -type d -name "site"|grep "/src"|grep "./modules/"`
do
	echo "--> copying to $dir"
	cp -dfrp src/site/css $dir
	cp -dfrp src/site/images $dir
done

echo "-> done"
echo
