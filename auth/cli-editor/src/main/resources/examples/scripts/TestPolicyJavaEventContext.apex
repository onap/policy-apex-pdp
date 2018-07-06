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

model create name=Test_PolicyModel

schema create name=JavaBoolean_type   flavour=Java schema=java.lang.Boolean
schema create name=JavaByte_type      flavour=Java schema=java.lang.Byte
schema create name=JavaShort_type     flavour=Java schema=java.lang.Short
schema create name=JavaInteger_type   flavour=Java schema=java.lang.Integer
schema create name=JavaLong_type      flavour=Java schema=java.lang.Long
schema create name=JavaFloat_type     flavour=Java schema=java.lang.Float
schema create name=JavaDouble_type    flavour=Java schema=java.lang.Double
schema create name=JavaString_type    flavour=Java schema=java.lang.String
schema create name=CustomBoolean_type flavour=Java schema=org.onap.policy.apex.context.test.concepts.TestContextBooleanItem
schema create name=CustomByte_type    flavour=Java schema=org.onap.policy.apex.context.test.concepts.TestContextByteItem
schema create name=CustomInteger_type flavour=Java schema=org.onap.policy.apex.context.test.concepts.TestContextIntItem
schema create name=CustomLong_type    flavour=Java schema=org.onap.policy.apex.context.test.concepts.TestContextLongItem
schema create name=CustomFloat_type   flavour=Java schema=org.onap.policy.apex.context.test.concepts.TestContextFloatItem
schema create name=CustomDouble_type  flavour=Java schema=org.onap.policy.apex.context.test.concepts.TestContextDoubleItem
schema create name=CustomString_type  flavour=Java schema=org.onap.policy.apex.context.test.concepts.TestContextStringItem
schema create name=CustomJLong_type   flavour=Java schema=org.onap.policy.apex.context.test.concepts.TestContextLongObjectItem
schema create name=CustomDate0_type   flavour=Java schema=org.onap.policy.apex.context.test.concepts.TestContextDateItem
schema create name=CustomDate1_type   flavour=Java schema=org.onap.policy.apex.context.test.concepts.TestContextDateTzItem
schema create name=CustomDate2_type   flavour=Java schema=org.onap.policy.apex.context.test.concepts.TestContextDateLocaleItem
schema create name=CustomSet_type     flavour=Java schema=org.onap.policy.apex.context.test.concepts.TestContextTreeSetItem
schema create name=CustomMap_type     flavour=Java schema=org.onap.policy.apex.context.test.concepts.TestContextTreeMapItem

event create name=Test_InputEvent nameSpace=org.onap.policy.apex.test source=External target=Apex

event parameter create name=Test_InputEvent parName=JavaBoolean   schemaName=JavaBoolean_type
event parameter create name=Test_InputEvent parName=JavaByte      schemaName=JavaByte_type
event parameter create name=Test_InputEvent parName=JavaShort     schemaName=JavaShort_type
event parameter create name=Test_InputEvent parName=JavaInteger   schemaName=JavaInteger_type
event parameter create name=Test_InputEvent parName=JavaLong      schemaName=JavaLong_type
event parameter create name=Test_InputEvent parName=JavaFloat     schemaName=JavaFloat_type
event parameter create name=Test_InputEvent parName=JavaDouble    schemaName=JavaDouble_type
event parameter create name=Test_InputEvent parName=JavaString    schemaName=JavaString_type
event parameter create name=Test_InputEvent parName=CustomBoolean schemaName=CustomBoolean_type
event parameter create name=Test_InputEvent parName=CustomByte    schemaName=CustomByte_type
event parameter create name=Test_InputEvent parName=CustomInteger schemaName=CustomInteger_type
event parameter create name=Test_InputEvent parName=CustomLong    schemaName=CustomLong_type
event parameter create name=Test_InputEvent parName=CustomFloat   schemaName=CustomFloat_type
event parameter create name=Test_InputEvent parName=CustomDouble  schemaName=CustomDouble_type
event parameter create name=Test_InputEvent parName=CustomString  schemaName=CustomString_type
event parameter create name=Test_InputEvent parName=CustomJLong   schemaName=CustomJLong_type
event parameter create name=Test_InputEvent parName=CustomDate0   schemaName=CustomDate0_type
event parameter create name=Test_InputEvent parName=CustomDate1   schemaName=CustomDate1_type
event parameter create name=Test_InputEvent parName=CustomDate2   schemaName=CustomDate2_type
event parameter create name=Test_InputEvent parName=CustomSet     schemaName=CustomSet_type
event parameter create name=Test_InputEvent parName=CustomMap     schemaName=CustomMap_type

event create name=Test_OutputEvent nameSpace=org.onap.policy.apex.test source=External target=Apex

event parameter create name=Test_OutputEvent parName=JavaBoolean   schemaName=JavaBoolean_type
event parameter create name=Test_OutputEvent parName=JavaByte      schemaName=JavaByte_type
event parameter create name=Test_OutputEvent parName=JavaShort     schemaName=JavaShort_type
event parameter create name=Test_OutputEvent parName=JavaInteger   schemaName=JavaInteger_type
event parameter create name=Test_OutputEvent parName=JavaLong      schemaName=JavaLong_type
event parameter create name=Test_OutputEvent parName=JavaFloat     schemaName=JavaFloat_type
event parameter create name=Test_OutputEvent parName=JavaDouble    schemaName=JavaDouble_type
event parameter create name=Test_OutputEvent parName=JavaString    schemaName=JavaString_type
event parameter create name=Test_OutputEvent parName=CustomBoolean schemaName=CustomBoolean_type
event parameter create name=Test_OutputEvent parName=CustomByte    schemaName=CustomByte_type
event parameter create name=Test_OutputEvent parName=CustomInteger schemaName=CustomInteger_type
event parameter create name=Test_OutputEvent parName=CustomLong    schemaName=CustomLong_type
event parameter create name=Test_OutputEvent parName=CustomFloat   schemaName=CustomFloat_type
event parameter create name=Test_OutputEvent parName=CustomDouble  schemaName=CustomDouble_type
event parameter create name=Test_OutputEvent parName=CustomString  schemaName=CustomString_type
event parameter create name=Test_OutputEvent parName=CustomJLong   schemaName=CustomJLong_type
event parameter create name=Test_OutputEvent parName=CustomDate0   schemaName=CustomDate0_type
event parameter create name=Test_OutputEvent parName=CustomDate1   schemaName=CustomDate1_type
event parameter create name=Test_OutputEvent parName=CustomDate2   schemaName=CustomDate2_type
event parameter create name=Test_OutputEvent parName=CustomSet     schemaName=CustomSet_type
event parameter create name=Test_OutputEvent parName=CustomMap     schemaName=CustomMap_type

album create name=JavaBooleanAlbum   scope=policy writable=true schemaName=JavaBoolean_type
album create name=JavaByteAlbum      scope=policy writable=true schemaName=JavaByte_type
album create name=JavaShortAlbum     scope=policy writable=true schemaName=JavaShort_type
album create name=JavaIntegerAlbum   scope=policy writable=true schemaName=JavaInteger_type
album create name=JavaLongAlbum      scope=policy writable=true schemaName=JavaLong_type
album create name=JavaFloatAlbum     scope=policy writable=true schemaName=JavaFloat_type
album create name=JavaDoubleAlbum    scope=policy writable=true schemaName=JavaDouble_type
album create name=JavaStringAlbum    scope=policy writable=true schemaName=JavaString_type
album create name=CustomBooleanAlbum scope=policy writable=true schemaName=CustomBoolean_type
album create name=CustomByteAlbum    scope=policy writable=true schemaName=CustomByte_type
album create name=CustomIntegerAlbum scope=policy writable=true schemaName=CustomInteger_type
album create name=CustomLongAlbum    scope=policy writable=true schemaName=CustomLong_type
album create name=CustomFloatAlbum   scope=policy writable=true schemaName=CustomFloat_type
album create name=CustomDoubleAlbum  scope=policy writable=true schemaName=CustomDouble_type
album create name=CustomStringAlbum  scope=policy writable=true schemaName=CustomString_type
album create name=CustomJLongAlbum   scope=policy writable=true schemaName=CustomJLong_type
album create name=CustomDate0Album   scope=policy writable=true schemaName=CustomDate0_type
album create name=CustomDate1Album   scope=policy writable=true schemaName=CustomDate1_type
album create name=CustomDate2Album   scope=policy writable=true schemaName=CustomDate2_type
album create name=CustomSetAlbum     scope=policy writable=true schemaName=CustomSet_type
album create name=CustomMapAlbum     scope=policy writable=true schemaName=CustomMap_type

task create name=Test_Task

task inputfield create name=Test_Task fieldName=JavaBoolean   schemaName=JavaBoolean_type
task inputfield create name=Test_Task fieldName=JavaByte      schemaName=JavaByte_type
task inputfield create name=Test_Task fieldName=JavaShort     schemaName=JavaShort_type
task inputfield create name=Test_Task fieldName=JavaInteger   schemaName=JavaInteger_type
task inputfield create name=Test_Task fieldName=JavaLong      schemaName=JavaLong_type
task inputfield create name=Test_Task fieldName=JavaFloat     schemaName=JavaFloat_type
task inputfield create name=Test_Task fieldName=JavaDouble    schemaName=JavaDouble_type
task inputfield create name=Test_Task fieldName=JavaString    schemaName=JavaString_type
task inputfield create name=Test_Task fieldName=CustomBoolean schemaName=CustomBoolean_type
task inputfield create name=Test_Task fieldName=CustomByte    schemaName=CustomByte_type
task inputfield create name=Test_Task fieldName=CustomInteger schemaName=CustomInteger_type
task inputfield create name=Test_Task fieldName=CustomLong    schemaName=CustomLong_type
task inputfield create name=Test_Task fieldName=CustomFloat   schemaName=CustomFloat_type
task inputfield create name=Test_Task fieldName=CustomDouble  schemaName=CustomDouble_type
task inputfield create name=Test_Task fieldName=CustomString  schemaName=CustomString_type
task inputfield create name=Test_Task fieldName=CustomJLong   schemaName=CustomJLong_type
task inputfield create name=Test_Task fieldName=CustomDate0   schemaName=CustomDate0_type
task inputfield create name=Test_Task fieldName=CustomDate1   schemaName=CustomDate1_type
task inputfield create name=Test_Task fieldName=CustomDate2   schemaName=CustomDate2_type
task inputfield create name=Test_Task fieldName=CustomSet     schemaName=CustomSet_type
task inputfield create name=Test_Task fieldName=CustomMap     schemaName=CustomMap_type

task outputfield create name=Test_Task fieldName=JavaBoolean   schemaName=JavaBoolean_type
task outputfield create name=Test_Task fieldName=JavaByte      schemaName=JavaByte_type
task outputfield create name=Test_Task fieldName=JavaShort     schemaName=JavaShort_type
task outputfield create name=Test_Task fieldName=JavaInteger   schemaName=JavaInteger_type
task outputfield create name=Test_Task fieldName=JavaLong      schemaName=JavaLong_type
task outputfield create name=Test_Task fieldName=JavaFloat     schemaName=JavaFloat_type
task outputfield create name=Test_Task fieldName=JavaDouble    schemaName=JavaDouble_type
task outputfield create name=Test_Task fieldName=JavaString    schemaName=JavaString_type
task outputfield create name=Test_Task fieldName=CustomBoolean schemaName=CustomBoolean_type
task outputfield create name=Test_Task fieldName=CustomByte    schemaName=CustomByte_type
task outputfield create name=Test_Task fieldName=CustomInteger schemaName=CustomInteger_type
task outputfield create name=Test_Task fieldName=CustomLong    schemaName=CustomLong_type
task outputfield create name=Test_Task fieldName=CustomFloat   schemaName=CustomFloat_type
task outputfield create name=Test_Task fieldName=CustomDouble  schemaName=CustomDouble_type
task outputfield create name=Test_Task fieldName=CustomString  schemaName=CustomString_type
task outputfield create name=Test_Task fieldName=CustomJLong   schemaName=CustomJLong_type
task outputfield create name=Test_Task fieldName=CustomDate0   schemaName=CustomDate0_type
task outputfield create name=Test_Task fieldName=CustomDate1   schemaName=CustomDate1_type
task outputfield create name=Test_Task fieldName=CustomDate2   schemaName=CustomDate2_type
task outputfield create name=Test_Task fieldName=CustomSet     schemaName=CustomSet_type
task outputfield create name=Test_Task fieldName=CustomMap     schemaName=CustomMap_type

task contextref create name=Test_Task albumName=JavaBooleanAlbum
task contextref create name=Test_Task albumName=JavaByteAlbum
task contextref create name=Test_Task albumName=JavaShortAlbum
task contextref create name=Test_Task albumName=JavaIntegerAlbum
task contextref create name=Test_Task albumName=JavaLongAlbum
task contextref create name=Test_Task albumName=JavaFloatAlbum
task contextref create name=Test_Task albumName=JavaDoubleAlbum
task contextref create name=Test_Task albumName=JavaStringAlbum
task contextref create name=Test_Task albumName=CustomBooleanAlbum
task contextref create name=Test_Task albumName=CustomByteAlbum
task contextref create name=Test_Task albumName=CustomIntegerAlbum
task contextref create name=Test_Task albumName=CustomLongAlbum
task contextref create name=Test_Task albumName=CustomFloatAlbum
task contextref create name=Test_Task albumName=CustomDoubleAlbum
task contextref create name=Test_Task albumName=CustomStringAlbum
task contextref create name=Test_Task albumName=CustomJLongAlbum
task contextref create name=Test_Task albumName=CustomDate0Album
task contextref create name=Test_Task albumName=CustomDate1Album
task contextref create name=Test_Task albumName=CustomDate2Album
task contextref create name=Test_Task albumName=CustomSetAlbum
task contextref create name=Test_Task albumName=CustomMapAlbum

task logic create name=Test_Task logicFlavour=JAVASCRIPT logic=LS
executor.logger.debug(executor.subject.id);

executor.logger.debug(executor.inFields);

executor.getContextAlbum("JavaBooleanAlbum"  ).put("JavaBoolean"  , executor.inFields.get("JavaBoolean"  ));
executor.getContextAlbum("JavaByteAlbum"     ).put("JavaByte"     , executor.inFields.get("JavaByte"     ));
executor.getContextAlbum("JavaShortAlbum"    ).put("JavaShort"    , executor.inFields.get("JavaShort"    ));
executor.getContextAlbum("JavaIntegerAlbum"  ).put("JavaInteger"  , executor.inFields.get("JavaInteger"  ));
executor.getContextAlbum("JavaLongAlbum"     ).put("JavaLong"     , executor.inFields.get("JavaLong"     ));
executor.getContextAlbum("JavaFloatAlbum"    ).put("JavaFloat"    , executor.inFields.get("JavaFloat"    ));
executor.getContextAlbum("JavaDoubleAlbum"   ).put("JavaDouble"   , executor.inFields.get("JavaDouble"   ));
executor.getContextAlbum("JavaStringAlbum"   ).put("JavaString"   , executor.inFields.get("JavaString"   ));
executor.getContextAlbum("CustomBooleanAlbum").put("CustomBoolean", executor.inFields.get("CustomBoolean"));
executor.getContextAlbum("CustomByteAlbum"   ).put("CustomByte"   , executor.inFields.get("CustomByte"   ));
executor.getContextAlbum("CustomIntegerAlbum").put("CustomInteger", executor.inFields.get("CustomInteger"));
executor.getContextAlbum("CustomLongAlbum"   ).put("CustomLong"   , executor.inFields.get("CustomLong"   ));
executor.getContextAlbum("CustomFloatAlbum"  ).put("CustomFloat"  , executor.inFields.get("CustomFloat"  ));
executor.getContextAlbum("CustomDoubleAlbum" ).put("CustomDouble" , executor.inFields.get("CustomDouble" ));
executor.getContextAlbum("CustomStringAlbum" ).put("CustomString" , executor.inFields.get("CustomString" ));
executor.getContextAlbum("CustomJLongAlbum"  ).put("CustomJLong"  , executor.inFields.get("CustomJLong"  ));
executor.getContextAlbum("CustomDate0Album"  ).put("CustomDate0"  , executor.inFields.get("CustomDate0"  ));
executor.getContextAlbum("CustomDate1Album"  ).put("CustomDate1"  , executor.inFields.get("CustomDate1"  ));
executor.getContextAlbum("CustomDate2Album"  ).put("CustomDate2"  , executor.inFields.get("CustomDate2"  ));
executor.getContextAlbum("CustomSetAlbum"    ).put("CustomSet"    , executor.inFields.get("CustomSet"    ));
executor.getContextAlbum("CustomMapAlbum"    ).put("CustomMap"    , executor.inFields.get("CustomMap"    ));

var javaBoolean   = executor.getContextAlbum("JavaBooleanAlbum"  ).get("JavaBoolean"  );
var javaByte      = executor.getContextAlbum("JavaByteAlbum"     ).get("JavaByte"     );
var javaShort     = executor.getContextAlbum("JavaShortAlbum"    ).get("JavaShort"    );
var javaInteger   = executor.getContextAlbum("JavaIntegerAlbum"  ).get("JavaInteger"  );
var javaLong      = executor.getContextAlbum("JavaLongAlbum"     ).get("JavaLong"     );
var javaFloat     = executor.getContextAlbum("JavaFloatAlbum"    ).get("JavaFloat"    );
var javaDouble    = executor.getContextAlbum("JavaDoubleAlbum"   ).get("JavaDouble"   );
var javaString    = executor.getContextAlbum("JavaStringAlbum"   ).get("JavaString"   );
var customBoolean = executor.getContextAlbum("CustomBooleanAlbum").get("CustomBoolean");
var customByte    = executor.getContextAlbum("CustomByteAlbum"   ).get("CustomByte"   );
var customInteger = executor.getContextAlbum("CustomIntegerAlbum").get("CustomInteger");
var customLong    = executor.getContextAlbum("CustomLongAlbum"   ).get("CustomLong"   );
var customFloat   = executor.getContextAlbum("CustomFloatAlbum"  ).get("CustomFloat"  );
var customDouble  = executor.getContextAlbum("CustomDoubleAlbum" ).get("CustomDouble" );
var customString  = executor.getContextAlbum("CustomStringAlbum" ).get("CustomString" );
var customJLong   = executor.getContextAlbum("CustomJLongAlbum"  ).get("CustomJLong"  );
var customDate0   = executor.getContextAlbum("CustomDate0Album"  ).get("CustomDate0"  );
var customDate1   = executor.getContextAlbum("CustomDate1Album"  ).get("CustomDate1"  );
var customDate2   = executor.getContextAlbum("CustomDate2Album"  ).get("CustomDate2"  );
var customSet     = executor.getContextAlbum("CustomSetAlbum"    ).get("CustomSet"    );
var customMap     = executor.getContextAlbum("CustomMapAlbum"    ).get("CustomMap"    );

javaBoolean    = !javaBoolean;
javaByte      += 1;
javaShortType  = Java.type("java.lang.Short");
javaShort      = new javaShortType(javaShort + 1);
javaInteger   += 1;
javaLong      += 1;
javaFloat     += 0.99;
javaDouble    += 0.99;
javaString     = javaString + " added to end of string";
customBoolean.setFlag(!customBoolean);
customByte   .setByteValue  (customByte   .getByteValue()   + 1);
customInteger.setIntValue   (customInteger.getIntValue()    + 1);
customLong   .setLongValue  (customLong   .getLongValue()   + 1);
customFloat  .setFloatValue (customFloat  .getFloatValue()  + 1);
customDouble .setDoubleValue(customDouble .getDoubleValue() + 1);
customString .setStringValue(customString .getStringValue() + " added to end of string");
customJLong  .setLongValue  (customJLong  .getLongValue()   + 1);

customDate0Type = Java.type("org.onap.policy.apex.context.test.concepts.TestContextDateItem");
customDate0 = new customDate0Type(1499868391);

customDate1Type = Java.type("org.onap.policy.apex.context.test.concepts.TestContextDateTzItem");
customDate1 = new customDate1Type();
customDate1.setDateValue(customDate0);

customDate2Type = Java.type("org.onap.policy.apex.context.test.concepts.TestContextDateLocaleItem");
customDate2 = new customDate2Type();
customDate2.setDateValue(customDate0);

customSet.getSetValue().add("with");
customSet.getSetValue().add("a");
customSet.getSetValue().add("beard");

customMap.getMapValue().put("love", "hate");
customMap.getMapValue().put("summer", "winter");
customMap.getMapValue().put("good", "bad");

executor.getContextAlbum("JavaBooleanAlbum"  ).put("JavaBoolean"  , javaBoolean);
executor.getContextAlbum("JavaByteAlbum"     ).put("JavaByte"     , javaByte);
executor.getContextAlbum("JavaShortAlbum"    ).put("JavaShort"    , javaShort);
executor.getContextAlbum("JavaIntegerAlbum"  ).put("JavaInteger"  , javaInteger);
executor.getContextAlbum("JavaLongAlbum"     ).put("JavaLong"     , javaLong);
executor.getContextAlbum("JavaFloatAlbum"    ).put("JavaFloat"    , javaFloat);
executor.getContextAlbum("JavaDoubleAlbum"   ).put("JavaDouble"   , javaDouble);
executor.getContextAlbum("JavaStringAlbum"   ).put("JavaString"   , javaString);
executor.getContextAlbum("CustomBooleanAlbum").put("CustomBoolean", customBoolean);
executor.getContextAlbum("CustomByteAlbum"   ).put("CustomByte"   , customByte);
executor.getContextAlbum("CustomIntegerAlbum").put("CustomInteger", customInteger);
executor.getContextAlbum("CustomLongAlbum"   ).put("CustomLong"   , customLong);
executor.getContextAlbum("CustomFloatAlbum"  ).put("CustomFloat"  , customFloat);
executor.getContextAlbum("CustomDoubleAlbum" ).put("CustomDouble" , customDouble);
executor.getContextAlbum("CustomStringAlbum" ).put("CustomString" , customString);
executor.getContextAlbum("CustomJLongAlbum"  ).put("CustomJLong"  , customJLong);
executor.getContextAlbum("CustomDate0Album"  ).put("CustomDate0"  , customDate0);
executor.getContextAlbum("CustomDate1Album"  ).put("CustomDate1"  , customDate1);
executor.getContextAlbum("CustomDate2Album"  ).put("CustomDate2"  , customDate2);
executor.getContextAlbum("CustomSetAlbum"    ).put("CustomSet"    , customSet);
executor.getContextAlbum("CustomMapAlbum"    ).put("CustomMap"    , customMap);

executor.outFields.put("JavaBoolean"  , executor.getContextAlbum("JavaBooleanAlbum"  ).get("JavaBoolean"  ));
executor.outFields.put("JavaByte"     , executor.getContextAlbum("JavaByteAlbum"     ).get("JavaByte"     ));
executor.outFields.put("JavaShort"    , executor.getContextAlbum("JavaShortAlbum"    ).get("JavaShort"    ));
executor.outFields.put("JavaInteger"  , executor.getContextAlbum("JavaIntegerAlbum"  ).get("JavaInteger"  ));
executor.outFields.put("JavaLong"     , executor.getContextAlbum("JavaLongAlbum"     ).get("JavaLong"     ));
executor.outFields.put("JavaFloat"    , executor.getContextAlbum("JavaFloatAlbum"    ).get("JavaFloat"    ));
executor.outFields.put("JavaDouble"   , executor.getContextAlbum("JavaDoubleAlbum"   ).get("JavaDouble"   ));
executor.outFields.put("JavaString"   , executor.getContextAlbum("JavaStringAlbum"   ).get("JavaString"   ));
executor.outFields.put("CustomBoolean", executor.getContextAlbum("CustomBooleanAlbum").get("CustomBoolean"));
executor.outFields.put("CustomByte"   , executor.getContextAlbum("CustomByteAlbum"   ).get("CustomByte"   ));
executor.outFields.put("CustomInteger", executor.getContextAlbum("CustomIntegerAlbum").get("CustomInteger"));
executor.outFields.put("CustomLong"   , executor.getContextAlbum("CustomLongAlbum"   ).get("CustomLong"   ));
executor.outFields.put("CustomFloat"  , executor.getContextAlbum("CustomFloatAlbum"  ).get("CustomFloat"  ));
executor.outFields.put("CustomDouble" , executor.getContextAlbum("CustomDoubleAlbum" ).get("CustomDouble" ));
executor.outFields.put("CustomString" , executor.getContextAlbum("CustomStringAlbum" ).get("CustomString" ));
executor.outFields.put("CustomJLong"  , executor.getContextAlbum("CustomJLongAlbum"  ).get("CustomJLong"  ));
executor.outFields.put("CustomDate0"  , executor.getContextAlbum("CustomDate0Album"  ).get("CustomDate0"  ));
executor.outFields.put("CustomDate1"  , executor.getContextAlbum("CustomDate1Album"  ).get("CustomDate1"  ));
executor.outFields.put("CustomDate2"  , executor.getContextAlbum("CustomDate2Album"  ).get("CustomDate2"  ));
executor.outFields.put("CustomSet"    , executor.getContextAlbum("CustomSetAlbum"    ).get("CustomSet"    ));
executor.outFields.put("CustomMap"    , executor.getContextAlbum("CustomMapAlbum"    ).get("CustomMap"    )); 

executor.logger.debug(executor.outFields);

var returnValueType = Java.type("java.lang.Boolean");
var returnValue = new returnValueType(true);

LE

policy create name=Test_Policy template=FREEFORM firstState=OnlyState

policy state create name=Test_Policy stateName=OnlyState triggerName=Test_InputEvent defaultTaskName=Test_Task
policy state output create name=Test_Policy stateName=OnlyState outputName=OnlyState2Out eventName=Test_OutputEvent
policy state taskref create name=Test_Policy stateName=OnlyState taskName=Test_Task outputType=DIRECT outputName=OnlyState2Out
