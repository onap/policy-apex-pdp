#!/usr/bin/env bash

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

MOD_SCRIPT_NAME=`basename $0`

if [ $# -eq 0 ]; then
    echo ""
    echo "$MOD_SCRIPT_NAME - run VLC that streams video"
    echo ""
    echo "       Usage:  $MOD_SCRIPT_NAME [video file]"
    echo ""
    exit
fi

vlc-wrapper -vvv $1 --sout "#duplicate{dst=rtp{dst=10.0.0.4,port=5004,mux=ts},dst=display}" --sout-keep -q

