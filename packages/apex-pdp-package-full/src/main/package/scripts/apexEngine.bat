:: ============LICENSE_START=======================================================
::  Copyright (C) 2016-2018 Ericsson. All rights reserved.
:: ================================================================================
:: Licensed under the Apache License, Version 2.0 (the "License");
:: you may not use this file except in compliance with the License.
:: You may obtain a copy of the License at
:: 
::      http://www.apache.org/licenses/LICENSE-2.0
:: 
:: Unless required by applicable law or agreed to in writing, software
:: distributed under the License is distributed on an "AS IS" BASIS,
:: WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
:: See the License for the specific language governing permissions and
:: limitations under the License.
:: 
:: SPDX-License-Identifier: Apache-2.0
:: ============LICENSE_END=========================================================

::
:: Script to run the APEX, calls apexApps.bat
::
:: @package    org.onap.policy.apex
:: @author     Sven van der Meer <sven.van.der.meer@ericsson.com>
:: @version    v2.0.0

::
:: DO NOT CHANGE CODE BELOW, unless you know what you are doing
::

@echo off
setlocal enableDelayedExpansion


if defined APEX_HOME (
	if exist "%APEX_HOME%\" (
		set _dummy=dir
	) else (
		echo[
		echo Apex directory 'APEX_HOME' not a directory
		echo Please set environment for 'APEX_HOME'
		echo[
		exit /b
	)
) else (
	echo[
	echo Apex directory 'APEX_HOME' not set
	echo Please set environment for 'APEX_HOME'
	echo[
	exit /b
)

%APEX_HOME%\bin\apexApps.bat engine %*
