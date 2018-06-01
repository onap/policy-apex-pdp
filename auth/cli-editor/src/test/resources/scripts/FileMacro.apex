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

#MACROFILE:"src/test/resources/macros/MacroFile0.apexmacro"
#MACROFILE: "src/test/resources/macros/MacroFile1.apexmacro"
#MACROFILE:src/test/resources/macros/MacroFile0.apexmacro
#MACROFILE: src/test/resources/macros/MacroFile0.apexmacro  
#MACROFILE:" src/test/resources/macros/MacroFile1.apexmacro"
#MACROFILE: "src/test/resources/macros/MacroFile1.apexmacro "
#MACROFILE:                     "src/test/resources/macros/MacroFile2.apexmacro"
#MACROFILE:                     "src/test/resources/macros/MacroFile3.apexmacro"                      
#MACROFILE:                     "src/test/resources/macros/MacroFile4.apexmacro"                      # This is a comment
#MACROFILE:
#MACROFILE:                             
#MACROFILE
MACROFILE:
#MACROFILE: "src/test/resources/macros/MacroFileNonExistant.ApexMacro"
schema create #MACROFILE:"src/test/resources/macros/MacroFile5.apexmacro" #MACROFILE: "src/test/resources/macros/MacroFile6.apexmacro"

task create name=dummyTask1
task logic create name=dummyTask1 logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/test/resources/macros/MacroFile7.apexmacro"
LE

task create name=dummyTask2
task logic create name=dummyTask2 logicFlavour=JAVASCRIPT logic=LS
x=x+1
#MACROFILE:"src/test/resources/macros/MacroFile8.apexmacro"
x=x+1
#MACROFILE:"src/test/resources/macros/MacroFile9.apexmacro"
x=x+1
LE
