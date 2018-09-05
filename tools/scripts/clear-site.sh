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
## Script to clear all created site artifacts, so all target/site for all modules plus target/ad-site on parrent.
## Call -h for help
##
## @author     Sven van der Meer <sven.van.der.meer@ericsson.com>
## @version    v2.0.0


##
## DO NOT CHANGE CODE BELOW, unless you know what you are doing
##

## script name for output
MOD_SCRIPT_NAME=`basename $0`


##
## Help screen and exit condition (i.e. too few arguments)
##
Help()
{
	echo ""
	echo "$MOD_SCRIPT_NAME - remove all generated site artifacts."
	echo ""
	echo "       Usage:  $MOD_SCRIPT_NAME [options]"
	echo ""
	echo "       Options"
	echo "         -x          - execute the delete actions"
	echo "         -h          - this help screen"
	echo ""
	echo ""
	exit 255;
}
if [ $# -eq 0 ]; then
	Help
fi

while [ $# -gt 0 ]
do
	case $1 in
		# -x do clear
		-x)
			echo 
			echo "$MOD_SCRIPT_NAME: removing generated sites in all modules"
			for dir in `find -type d -name "site"|grep "/target/"`
			do
				echo "--> removing $dir"
				rm -fr $dir
			done
			echo "--> removing target/ad-site"
			rm -fr target/ad-site
			exit
		;;

		#-h prints help and exists
		-h)	Help;exit 0;;

		*)	echo "$MOD_SCRIPT_NAME: undefined CLI option - $1"; exit 255;;
	esac
done

