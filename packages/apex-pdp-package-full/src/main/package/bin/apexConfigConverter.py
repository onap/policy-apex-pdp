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

import sys
import json

if (len(sys.argv) != 3):
    print "usage: apexConfigConverter.py old_config_file.json new_config_file.json"
    sys.exit(0)

with open(sys.argv[1]) as data_file:    
    config = json.load(data_file)

# Get the old carrier technology parameters
oldCCTP = config["consumerCarrierTechnologyParameters"]
oldCEPP = config["consumerEventProtocolParameters"]
oldPCTP = config["producerCarrierTechnologyParameters"]
oldPEPP = config["producerEventProtocolParameters"]

# Delete the old carrier technology parameters from the configuration
del config["consumerCarrierTechnologyParameters"]
del config["consumerEventProtocolParameters"]
del config["producerCarrierTechnologyParameters"]
del config["producerEventProtocolParameters"]

# Create event inputs and event outputs from the old parameters
aConsumer = {}
aConsumer['carrierTechnologyParameters'] = oldCCTP
aConsumer['eventProtocolParameters']     = oldCEPP

aProducer = {}
aProducer['carrierTechnologyParameters'] = oldPCTP
aProducer['eventProtocolParameters']     = oldPEPP

# Create event input parameters and event output parameters
eventInputParameters  = {}
eventOutputParameters = {}

# Add the old consumer and producer to the input and putput parameters
eventInputParameters ['aConsumer'] = aConsumer
eventOutputParameters['aProducer'] = aProducer

# Add the event input parameters and event output parameters to the configuraiton
config['eventInputParameters']  = eventInputParameters
config['eventOutputParameters'] = eventOutputParameters

# Output the new configuration
with open(sys.argv[2], 'w') as outfile:
    json.dump(config, outfile, sort_keys=True, indent=4)
