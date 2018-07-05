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

schema create name=AvroBoolean_type flavour=Avro schema=LS
{
  "type" : "boolean"
}
LE

schema create name=AvroByte_type flavour=Avro schema=LS
{
  "type" : "int"
}
LE

schema create name=AvroShort_type flavour=Avro schema=LS
{
  "type" : "int"
}
LE

schema create name=AvroInteger_type flavour=Avro schema=LS
{
  "type" : "int"
}
LE

schema create name=AvroLong_type flavour=Avro schema=LS
{
  "type" : "long"
}
LE

schema create name=AvroFloat_type flavour=Avro schema=LS
{
  "type" : "float"
}
LE

schema create name=AvroDouble_type flavour=Avro schema=LS
{
  "type" : "double"
}
LE

schema create name=AvroString_type flavour=Avro schema=LS
{
  "type" : "string"}
LE

schema create name=AvroMap_type flavour=Avro schema=LS
{
	"type": "map", "values": "string"
}
LE

schema create name=AvroArray_type flavour=Avro schema=LS
{
	"type": "array", "items": "string"
}
LE

schema create name=CustomBoolean_type flavour=Avro schema=LS
{
  "type"      : "record",
  "name"      : "TestContextBooleanItem",
  "namespace" : "org.onap.policy.apex.context.test.avro.concepts",
  "fields"    : [
    {"name": "flag", "type": "boolean"}
  ]
}
LE

schema create name=CustomByte_type flavour=Avro schema=LS
{
  "type"      : "record",
  "name"      : "TestContextByteItem",
  "namespace" : "org.onap.policy.apex.context.test.avro.concepts",
  "fields"    : [
    {"name": "byteValue", "type": "int"}
  ]
}
LE

schema create name=CustomInteger_type flavour=Avro schema=LS
{
  "type"      : "record",
  "name"      : "TestContextIntItem",
  "namespace" : "org.onap.policy.apex.context.test.avro.concepts",
  "fields"    : [
    {"name": "intValue", "type": "int"}
  ]
}
LE

schema create name=CustomLong_type flavour=Avro schema=LS
{
  "type"      : "record",
  "name"      : "TestContextLongItem",
  "namespace" : "org.onap.policy.apex.context.test.avro.concepts",
  "fields"    : [
    {"name": "longValue", "type": "long"}
  ]
}
LE

schema create name=CustomFloat_type flavour=Avro schema=LS
{
  "type"      : "record",
  "name"      : "TestContextFloatItem",
  "namespace" : "org.onap.policy.apex.context.test.avro.concepts",
  "fields"    : [
    {"name": "floatValue", "type": "float"}
  ]
}
LE

schema create name=CustomDouble_type flavour=Avro schema=LS
{
  "type"      : "record",
  "name"      : "TestContextDoubleItem",
  "namespace" : "org.onap.policy.apex.context.test.avro.concepts",
  "fields"    : [
    {"name": "doubleValue", "type": "double"}
  ]
}
LE

schema create name=CustomString_type flavour=Avro schema=LS
{
  "type"      : "record",
  "name"      : "TestContextStringItem",
  "namespace" : "org.onap.policy.apex.context.test.avro.concepts",
  "fields"    : [
    {"name": "stringValue", "type": "string"}
  ]
}
LE

schema create name=CustomALong_type flavour=Avro schema=LS
{
  "type"      : "record",
  "name"      : "TestContextLongObjectItem",
  "namespace" : "org.onap.policy.apex.context.test.avro.concepts",
  "fields"    : [
    {"name": "longValue", "type": "long"}
  ]
}
LE

schema create name=CustomDate0_type flavour=Avro schema=LS
{
  "type"      : "record",
  "name"      : "TestContextDateItem",
  "namespace" : "org.onap.policy.apex.context.test.avro.concepts",
  "fields"    : [
  	{"name": "time"       , "type": "long"},
  	{"name": "year"       , "type": "int"},
  	{"name": "month"      , "type": "int"},
  	{"name": "day"        , "type": "int"},
  	{"name": "hour"       , "type": "int"},
  	{"name": "minute"     , "type": "int"},
  	{"name": "second"     , "type": "int"},
  	{"name": "milliSecond", "type": "int"}
  ]
}
LE

schema create name=CustomDate1_type flavour=Avro schema=LS
{
  "type"      : "record",
  "name"      : "TestContextDateTzItem",
  "namespace" : "org.onap.policy.apex.context.test.avro.concepts",
  "fields"    : [
    {"name": "dateValue"     , "type": {
  		"type"      : "record",
 	 	"name"      : "TestContextItem008",
		"namespace" : "org.onap.policy.apex.context.test.avro.concepts",
		"fields"    : [
   			{"name": "time"       , "type": "long"},
  			{"name": "year"       , "type": "int"},
  			{"name": "month"      , "type": "int"},
 		 	{"name": "day"        , "type": "int"},
  			{"name": "hour"       , "type": "int"},
  			{"name": "minute"     , "type": "int"},
  			{"name": "second"     , "type": "int"},
  			{"name": "milliSecond", "type": "int"}
 		 ]
		}
	},
    {"name": "dst"           , "type": "boolean"},
    {"name": "timeZoneString", "type": "string"}
  ]
}
LE

schema create name=CustomDate2_type flavour=Avro schema=LS
{
  "type"      : "record",
  "name"      : "TestContextDateLocaleItem",
  "namespace" : "org.onap.policy.apex.context.test.avro.concepts",
  "fields"    : [
    {"name": "dateValue"     , "type": {
  		"type"      : "record",
 	 	"name"      : "TestContextDateItem",
		"namespace" : "org.onap.policy.apex.context.test.avro.concepts",
		"fields"    : [
    		{"name": "time"       , "type": "long"},
  			{"name": "year"       , "type": "int"},
  			{"name": "month"      , "type": "int"},
 		 	{"name": "day"        , "type": "int"},
  			{"name": "hour"       , "type": "int"},
  			{"name": "minute"     , "type": "int"},
  			{"name": "second"     , "type": "int"},
  			{"name": "milliSecond", "type": "int"}
 		 ]
		}
	},
    {"name": "dst"           , "type": "boolean"},
    {"name": "timeZoneString", "type": "string"},
    {"name": "utcOffset"     , "type": "int"},
    {"name": "localeLanguage", "type": "string"},
    {"name": "localeCountry" , "type": "string"}
  ]
}
LE

schema create name=CustomSet_type flavour=Avro schema=LS
{
  "type"      : "record",
  "name"      : "TestContextTreeSetItem",
  "namespace" : "org.onap.policy.apex.context.test.avro.concepts",
  "fields"    : [
    {"name": "setValue", "type": {
		"type": "array", "items": "string"
		}
	}
  ]
}
LE

schema create name=CustomMap_type flavour=Avro schema=LS
{
  "type"      : "record",
  "name"      : "TestContextTreeMapItem",
  "namespace" : "org.onap.policy.apex.context.test.avro.concepts",
  "fields"    : [
    {"name": "mapValue", "type": {
		"type": "map", "values": "string"
		}
	}
  ]
}
LE

event create name=Test_InputEvent nameSpace=org.onap.policy.apex.test source=External target=Apex

event parameter create name=Test_InputEvent parName=AvroBoolean   schemaName=AvroBoolean_type optional=false
event parameter create name=Test_InputEvent parName=AvroByte      schemaName=AvroByte_type    optional=false
event parameter create name=Test_InputEvent parName=AvroShort     schemaName=AvroShort_type
event parameter create name=Test_InputEvent parName=AvroInteger   schemaName=AvroInteger_type optional=false
event parameter create name=Test_InputEvent parName=AvroLong      schemaName=AvroLong_type
event parameter create name=Test_InputEvent parName=AvroFloat     schemaName=AvroFloat_type
event parameter create name=Test_InputEvent parName=AvroDouble    schemaName=AvroDouble_type  optional=false
event parameter create name=Test_InputEvent parName=AvroString    schemaName=AvroString_type
event parameter create name=Test_InputEvent parName=AvroMap       schemaName=AvroMap_type
event parameter create name=Test_InputEvent parName=AvroArray     schemaName=AvroArray_type
event parameter create name=Test_InputEvent parName=CustomBoolean schemaName=CustomBoolean_type
event parameter create name=Test_InputEvent parName=CustomByte    schemaName=CustomByte_type
event parameter create name=Test_InputEvent parName=CustomInteger schemaName=CustomInteger_type
event parameter create name=Test_InputEvent parName=CustomLong    schemaName=CustomLong_type
event parameter create name=Test_InputEvent parName=CustomFloat   schemaName=CustomFloat_type
event parameter create name=Test_InputEvent parName=CustomDouble  schemaName=CustomDouble_type
event parameter create name=Test_InputEvent parName=CustomString  schemaName=CustomString_type
event parameter create name=Test_InputEvent parName=CustomALong   schemaName=CustomALong_type
event parameter create name=Test_InputEvent parName=CustomDate0   schemaName=CustomDate0_type
event parameter create name=Test_InputEvent parName=CustomDate1   schemaName=CustomDate1_type
event parameter create name=Test_InputEvent parName=CustomDate2   schemaName=CustomDate2_type
event parameter create name=Test_InputEvent parName=CustomSet     schemaName=CustomSet_type
event parameter create name=Test_InputEvent parName=CustomMap     schemaName=CustomMap_type

event create name=Test_OutputEvent nameSpace=org.onap.policy.apex.test source=External target=Apex

event parameter create name=Test_OutputEvent parName=AvroBoolean   schemaName=AvroBoolean_type
event parameter create name=Test_OutputEvent parName=AvroByte      schemaName=AvroByte_type
event parameter create name=Test_OutputEvent parName=AvroShort     schemaName=AvroShort_type
event parameter create name=Test_OutputEvent parName=AvroInteger   schemaName=AvroInteger_type
event parameter create name=Test_OutputEvent parName=AvroLong      schemaName=AvroLong_type
event parameter create name=Test_OutputEvent parName=AvroFloat     schemaName=AvroFloat_type
event parameter create name=Test_OutputEvent parName=AvroDouble    schemaName=AvroDouble_type
event parameter create name=Test_OutputEvent parName=AvroString    schemaName=AvroString_type
event parameter create name=Test_OutputEvent parName=AvroMap       schemaName=AvroMap_type
event parameter create name=Test_OutputEvent parName=AvroArray     schemaName=AvroArray_type
event parameter create name=Test_OutputEvent parName=CustomBoolean schemaName=CustomBoolean_type
event parameter create name=Test_OutputEvent parName=CustomByte    schemaName=CustomByte_type
event parameter create name=Test_OutputEvent parName=CustomInteger schemaName=CustomInteger_type
event parameter create name=Test_OutputEvent parName=CustomLong    schemaName=CustomLong_type
event parameter create name=Test_OutputEvent parName=CustomFloat   schemaName=CustomFloat_type
event parameter create name=Test_OutputEvent parName=CustomDouble  schemaName=CustomDouble_type
event parameter create name=Test_OutputEvent parName=CustomString  schemaName=CustomString_type
event parameter create name=Test_OutputEvent parName=CustomALong   schemaName=CustomALong_type
event parameter create name=Test_OutputEvent parName=CustomDate0   schemaName=CustomDate0_type
event parameter create name=Test_OutputEvent parName=CustomDate1   schemaName=CustomDate1_type
event parameter create name=Test_OutputEvent parName=CustomDate2   schemaName=CustomDate2_type
event parameter create name=Test_OutputEvent parName=CustomSet     schemaName=CustomSet_type
event parameter create name=Test_OutputEvent parName=CustomMap     schemaName=CustomMap_type

album create name=AvroBooleanAlbum     scope=policy writable=true schemaName=AvroBoolean_type
album create name=AvroByteAlbum        scope=policy writable=true schemaName=AvroByte_type
album create name=AvroShortAlbum       scope=policy writable=true schemaName=AvroShort_type
album create name=AvroIntegerAlbum     scope=policy writable=true schemaName=AvroInteger_type
album create name=AvroLongAlbum        scope=policy writable=true schemaName=AvroLong_type
album create name=AvroFloatAlbum       scope=policy writable=true schemaName=AvroFloat_type
album create name=AvroDoubleAlbum      scope=policy writable=true schemaName=AvroDouble_type
album create name=AvroStringAlbum      scope=policy writable=true schemaName=AvroString_type
album create name=AvroMapAlbum         scope=policy writable=true schemaName=AvroMap_type
album create name=AvroArrayAlbum       scope=policy writable=true schemaName=AvroArray_type
album create name=CustomBooleanAlbum   scope=policy writable=true schemaName=CustomBoolean_type
album create name=CustomByteAlbum      scope=policy writable=true schemaName=CustomByte_type
album create name=CustomIntegerAlbum   scope=policy writable=true schemaName=CustomInteger_type
album create name=CustomLongAlbum      scope=policy writable=true schemaName=CustomLong_type
album create name=CustomFloatAlbum     scope=policy writable=true schemaName=CustomFloat_type
album create name=CustomDoubleAlbum    scope=policy writable=true schemaName=CustomDouble_type
album create name=CustomStringAlbum    scope=policy writable=true schemaName=CustomString_type
album create name=CustomALongAlbum     scope=policy writable=true schemaName=CustomALong_type
album create name=CustomDate0Album     scope=policy writable=true schemaName=CustomDate0_type
album create name=CustomDate1Album     scope=policy writable=true schemaName=CustomDate1_type
album create name=CustomDate2Album     scope=policy writable=true schemaName=CustomDate2_type
album create name=CustomSetAlbum       scope=policy writable=true schemaName=CustomSet_type
album create name=CustomMapAlbum       scope=policy writable=true schemaName=CustomMap_type

task create name=Test_Task

task inputfield create name=Test_Task fieldName=AvroBoolean   schemaName=AvroBoolean_type
task inputfield create name=Test_Task fieldName=AvroByte      schemaName=AvroByte_type
task inputfield create name=Test_Task fieldName=AvroShort     schemaName=AvroShort_type
task inputfield create name=Test_Task fieldName=AvroInteger   schemaName=AvroInteger_type
task inputfield create name=Test_Task fieldName=AvroLong      schemaName=AvroLong_type
task inputfield create name=Test_Task fieldName=AvroFloat     schemaName=AvroFloat_type
task inputfield create name=Test_Task fieldName=AvroDouble    schemaName=AvroDouble_type
task inputfield create name=Test_Task fieldName=AvroString    schemaName=AvroString_type
task inputfield create name=Test_Task fieldName=AvroMap       schemaName=AvroMap_type
task inputfield create name=Test_Task fieldName=AvroArray     schemaName=AvroArray_type
task inputfield create name=Test_Task fieldName=CustomBoolean schemaName=CustomBoolean_type
task inputfield create name=Test_Task fieldName=CustomByte    schemaName=CustomByte_type
task inputfield create name=Test_Task fieldName=CustomInteger schemaName=CustomInteger_type
task inputfield create name=Test_Task fieldName=CustomLong    schemaName=CustomLong_type
task inputfield create name=Test_Task fieldName=CustomFloat   schemaName=CustomFloat_type
task inputfield create name=Test_Task fieldName=CustomDouble  schemaName=CustomDouble_type
task inputfield create name=Test_Task fieldName=CustomString  schemaName=CustomString_type
task inputfield create name=Test_Task fieldName=CustomALong   schemaName=CustomALong_type
task inputfield create name=Test_Task fieldName=CustomDate0   schemaName=CustomDate0_type
task inputfield create name=Test_Task fieldName=CustomDate1   schemaName=CustomDate1_type
task inputfield create name=Test_Task fieldName=CustomDate2   schemaName=CustomDate2_type
task inputfield create name=Test_Task fieldName=CustomSet     schemaName=CustomSet_type
task inputfield create name=Test_Task fieldName=CustomMap     schemaName=CustomMap_type

task outputfield create name=Test_Task fieldName=AvroBoolean   schemaName=AvroBoolean_type
task outputfield create name=Test_Task fieldName=AvroByte      schemaName=AvroByte_type
task outputfield create name=Test_Task fieldName=AvroShort     schemaName=AvroShort_type
task outputfield create name=Test_Task fieldName=AvroInteger   schemaName=AvroInteger_type
task outputfield create name=Test_Task fieldName=AvroLong      schemaName=AvroLong_type
task outputfield create name=Test_Task fieldName=AvroFloat     schemaName=AvroFloat_type
task outputfield create name=Test_Task fieldName=AvroDouble    schemaName=AvroDouble_type
task outputfield create name=Test_Task fieldName=AvroString    schemaName=AvroString_type
task outputfield create name=Test_Task fieldName=AvroMap       schemaName=AvroMap_type
task outputfield create name=Test_Task fieldName=AvroArray     schemaName=AvroArray_type
task outputfield create name=Test_Task fieldName=CustomBoolean schemaName=CustomBoolean_type
task outputfield create name=Test_Task fieldName=CustomByte    schemaName=CustomByte_type
task outputfield create name=Test_Task fieldName=CustomInteger schemaName=CustomInteger_type
task outputfield create name=Test_Task fieldName=CustomLong    schemaName=CustomLong_type
task outputfield create name=Test_Task fieldName=CustomFloat   schemaName=CustomFloat_type
task outputfield create name=Test_Task fieldName=CustomDouble  schemaName=CustomDouble_type
task outputfield create name=Test_Task fieldName=CustomString  schemaName=CustomString_type
task outputfield create name=Test_Task fieldName=CustomALong   schemaName=CustomALong_type
task outputfield create name=Test_Task fieldName=CustomDate0   schemaName=CustomDate0_type
task outputfield create name=Test_Task fieldName=CustomDate1   schemaName=CustomDate1_type
task outputfield create name=Test_Task fieldName=CustomDate2   schemaName=CustomDate2_type
task outputfield create name=Test_Task fieldName=CustomSet     schemaName=CustomSet_type
task outputfield create name=Test_Task fieldName=CustomMap     schemaName=CustomMap_type

task contextref create name=Test_Task albumName=AvroBooleanAlbum
task contextref create name=Test_Task albumName=AvroByteAlbum
task contextref create name=Test_Task albumName=AvroShortAlbum
task contextref create name=Test_Task albumName=AvroIntegerAlbum
task contextref create name=Test_Task albumName=AvroLongAlbum
task contextref create name=Test_Task albumName=AvroFloatAlbum
task contextref create name=Test_Task albumName=AvroDoubleAlbum
task contextref create name=Test_Task albumName=AvroStringAlbum
task contextref create name=Test_Task albumName=AvroMapAlbum
task contextref create name=Test_Task albumName=AvroArrayAlbum
task contextref create name=Test_Task albumName=CustomBooleanAlbum
task contextref create name=Test_Task albumName=CustomByteAlbum
task contextref create name=Test_Task albumName=CustomIntegerAlbum
task contextref create name=Test_Task albumName=CustomLongAlbum
task contextref create name=Test_Task albumName=CustomFloatAlbum
task contextref create name=Test_Task albumName=CustomDoubleAlbum
task contextref create name=Test_Task albumName=CustomStringAlbum
task contextref create name=Test_Task albumName=CustomALongAlbum
task contextref create name=Test_Task albumName=CustomDate0Album
task contextref create name=Test_Task albumName=CustomDate1Album
task contextref create name=Test_Task albumName=CustomDate2Album
task contextref create name=Test_Task albumName=CustomSetAlbum
task contextref create name=Test_Task albumName=CustomMapAlbum

task logic create name=Test_Task logicFlavour=JAVASCRIPT logic=LS
executor.logger.debug(executor.subject.id);

executor.logger.debug(executor.inFields);

executor.getContextAlbum("AvroBooleanAlbum"  ).put("AvroBoolean"  , executor.inFields.get("AvroBoolean"));
executor.getContextAlbum("AvroByteAlbum"     ).put("AvroByte"     , executor.inFields.get("AvroByte"   ));
executor.getContextAlbum("AvroShortAlbum"    ).put("AvroShort"    , executor.inFields.get("AvroShort"  ));
executor.getContextAlbum("AvroIntegerAlbum"  ).put("AvroInteger"  , executor.inFields.get("AvroInteger"));
executor.getContextAlbum("AvroLongAlbum"     ).put("AvroLong"     , executor.inFields.get("AvroLong"   ));
executor.getContextAlbum("AvroFloatAlbum"    ).put("AvroFloat"    , executor.inFields.get("AvroFloat"  ));
executor.getContextAlbum("AvroDoubleAlbum"   ).put("AvroDouble"   , executor.inFields.get("AvroDouble" ));
executor.getContextAlbum("AvroStringAlbum"   ).put("AvroString"   , executor.inFields.get("AvroString" ));
executor.getContextAlbum("AvroMapAlbum"      ).put("AvroMap"      , executor.inFields.get("AvroMap"      ));
executor.getContextAlbum("AvroArrayAlbum"    ).put("AvroArray"    , executor.inFields.get("AvroArray"    ));
executor.getContextAlbum("CustomBooleanAlbum").put("CustomBoolean", executor.inFields.get("CustomBoolean"));
executor.getContextAlbum("CustomByteAlbum"   ).put("CustomByte"   , executor.inFields.get("CustomByte"   ));
executor.getContextAlbum("CustomIntegerAlbum").put("CustomInteger", executor.inFields.get("CustomInteger"));
executor.getContextAlbum("CustomLongAlbum"   ).put("CustomLong"   , executor.inFields.get("CustomLong"   ));
executor.getContextAlbum("CustomFloatAlbum"  ).put("CustomFloat"  , executor.inFields.get("CustomFloat"  ));
executor.getContextAlbum("CustomDoubleAlbum" ).put("CustomDouble" , executor.inFields.get("CustomDouble" ));
executor.getContextAlbum("CustomStringAlbum" ).put("CustomString" , executor.inFields.get("CustomString" ));
executor.getContextAlbum("CustomALongAlbum"  ).put("CustomALong"  , executor.inFields.get("CustomALong"  ));
executor.getContextAlbum("CustomDate0Album"  ).put("CustomDate0"  , executor.inFields.get("CustomDate0"  ));
executor.getContextAlbum("CustomDate1Album"  ).put("CustomDate1"  , executor.inFields.get("CustomDate1"  ));
executor.getContextAlbum("CustomDate2Album"  ).put("CustomDate2"  , executor.inFields.get("CustomDate2"  ));
executor.getContextAlbum("CustomSetAlbum"    ).put("CustomSet"    , executor.inFields.get("CustomSet"    ));
executor.getContextAlbum("CustomMapAlbum"    ).put("CustomMap"    , executor.inFields.get("CustomMap"    ));

var avroBoolean   = executor.getContextAlbum("AvroBooleanAlbum"  ).get("AvroBoolean"  );
var avroByte      = executor.getContextAlbum("AvroByteAlbum"     ).get("AvroByte"     );
var avroShort     = executor.getContextAlbum("AvroShortAlbum"    ).get("AvroShort"    );
var avroInteger   = executor.getContextAlbum("AvroIntegerAlbum"  ).get("AvroInteger"  );
var avroLong      = executor.getContextAlbum("AvroLongAlbum"     ).get("AvroLong"     );
var avroFloat     = executor.getContextAlbum("AvroFloatAlbum"    ).get("AvroFloat"    );
var avroDouble    = executor.getContextAlbum("AvroDoubleAlbum"   ).get("AvroDouble"   );
var avroString    = executor.getContextAlbum("AvroStringAlbum"   ).get("AvroString"   );
var customBoolean = executor.getContextAlbum("CustomBooleanAlbum").get("CustomBoolean");
var customByte    = executor.getContextAlbum("CustomByteAlbum"   ).get("CustomByte"   );
var customInteger = executor.getContextAlbum("CustomIntegerAlbum").get("CustomInteger");
var customLong    = executor.getContextAlbum("CustomLongAlbum"   ).get("CustomLong"   );
var customFloat   = executor.getContextAlbum("CustomFloatAlbum"  ).get("CustomFloat"  );
var customDouble  = executor.getContextAlbum("CustomDoubleAlbum" ).get("CustomDouble" );
var customString  = executor.getContextAlbum("CustomStringAlbum" ).get("CustomString" );
var customALong   = executor.getContextAlbum("CustomALongAlbum"  ).get("CustomALong"  );
var customDate0   = executor.getContextAlbum("CustomDate0Album"  ).get("CustomDate0"  );
var customDate1   = executor.getContextAlbum("CustomDate1Album"  ).get("CustomDate1"  );
var customDate2   = executor.getContextAlbum("CustomDate2Album"  ).get("CustomDate2"  );
var customSet     = executor.getContextAlbum("CustomSetAlbum"    ).get("CustomSet"    );
var customMap     = executor.getContextAlbum("CustomMapAlbum"    ).get("CustomMap"    );

javaIntegerType = Java.type("java.lang.Integer");
javaLongType    = Java.type("java.lang.Long");
javaFloatType   = Java.type("java.lang.Float");

avroBoolean    = !avroBoolean;
avroByte      = new javaIntegerType(avroByte    + 1);
avroShort     = new javaIntegerType(avroShort   + 1);
avroInteger   = new javaIntegerType(avroInteger + 1);
avroLong      = new javaLongType   (avroLong    + 1);
avroFloat     = new javaFloatType  (avroFloat   + 0.99);
avroDouble    += 0.99;
avroString     = avroString + " added to end of string";
customBoolean.put("flag",       !customBoolean.get("flag"));
customByte   .put("byteValue"  , customByte   .get("byteValue"  ) + 1);
customInteger.put("intValue"   , customInteger.get("intValue"   ) + 1);
customLong   .put("longValue"  , new javaLongType (customLong .get("longValue" ) + 1));
customFloat  .put("floatValue" , new javaFloatType(customFloat.get("floatValue") + 1.0));
customDouble .put("doubleValue", customDouble .get("doubleValue") + 1.0);
customString .put("stringValue", customString .get("stringValue") + " added to end of string");
customALong  .put("longValue"  , new javaLongType (customALong.get("longValue" ) + 1));

customDate0.put("year",  1922);
customDate0.put("month", 12);
customDate0.put("day",   6);

customDate1.put("dateValue", customDate0);
customDate2.put("dateValue", customDate0);

customSet.get("setValue").add("with");
customSet.get("setValue").add("a");
customSet.get("setValue").add("beard");

customMap.get("mapValue").put("love", "hate");
customMap.get("mapValue").put("summer", "winter");
customMap.get("mapValue").put("good", "bad");

executor.getContextAlbum("AvroBooleanAlbum"  ).put("AvroBoolean"  , avroBoolean);
executor.getContextAlbum("AvroByteAlbum"     ).put("AvroByte"     , avroByte);
executor.getContextAlbum("AvroShortAlbum"    ).put("AvroShort"    , avroShort);
executor.getContextAlbum("AvroIntegerAlbum"  ).put("AvroInteger"  , avroInteger);
executor.getContextAlbum("AvroLongAlbum"     ).put("AvroLong"     , avroLong);
executor.getContextAlbum("AvroFloatAlbum"    ).put("AvroFloat"    , avroFloat);
executor.getContextAlbum("AvroDoubleAlbum"   ).put("AvroDouble"   , avroDouble);
executor.getContextAlbum("AvroStringAlbum"   ).put("AvroString"   , avroString);
executor.getContextAlbum("CustomBooleanAlbum").put("CustomBoolean", customBoolean);
executor.getContextAlbum("CustomByteAlbum"   ).put("CustomByte"   , customByte);
executor.getContextAlbum("CustomIntegerAlbum").put("CustomInteger", customInteger);
executor.getContextAlbum("CustomLongAlbum"   ).put("CustomLong"   , customLong);
executor.getContextAlbum("CustomFloatAlbum"  ).put("CustomFloat"  , customFloat);
executor.getContextAlbum("CustomDoubleAlbum" ).put("CustomDouble" , customDouble);
executor.getContextAlbum("CustomStringAlbum" ).put("CustomString" , customString);
executor.getContextAlbum("CustomALongAlbum"  ).put("CustomALong"  , customALong);
executor.getContextAlbum("CustomDate0Album"  ).put("CustomDate0"  , customDate0);
executor.getContextAlbum("CustomDate1Album"  ).put("CustomDate1"  , customDate1);
executor.getContextAlbum("CustomDate2Album"  ).put("CustomDate2"  , customDate2);
executor.getContextAlbum("CustomSetAlbum"    ).put("CustomSet"    , customSet);
executor.getContextAlbum("CustomMapAlbum"    ).put("CustomMap"    , customMap);

executor.outFields.put("AvroBoolean"  , executor.getContextAlbum("AvroBooleanAlbum"  ).get("AvroBoolean"  ));
executor.outFields.put("AvroByte"     , executor.getContextAlbum("AvroByteAlbum"     ).get("AvroByte"     ));
executor.outFields.put("AvroShort"    , executor.getContextAlbum("AvroShortAlbum"    ).get("AvroShort"    ));
executor.outFields.put("AvroInteger"  , executor.getContextAlbum("AvroIntegerAlbum"  ).get("AvroInteger"  ));
executor.outFields.put("AvroLong"     , executor.getContextAlbum("AvroLongAlbum"     ).get("AvroLong"     ));
executor.outFields.put("AvroFloat"    , executor.getContextAlbum("AvroFloatAlbum"    ).get("AvroFloat"    ));
executor.outFields.put("AvroDouble"   , executor.getContextAlbum("AvroDoubleAlbum"   ).get("AvroDouble"   ));
executor.outFields.put("AvroString"   , executor.getContextAlbum("AvroStringAlbum"   ).get("AvroString"   ));
executor.outFields.put("AvroMap"      , executor.getContextAlbum("AvroMapAlbum"      ).get("AvroMap"      ));
executor.outFields.put("AvroArray"    , executor.getContextAlbum("AvroArrayAlbum"    ).get("AvroArray"    ));
executor.outFields.put("CustomBoolean", executor.getContextAlbum("CustomBooleanAlbum").get("CustomBoolean"));
executor.outFields.put("CustomByte"   , executor.getContextAlbum("CustomByteAlbum"   ).get("CustomByte"   ));
executor.outFields.put("CustomInteger", executor.getContextAlbum("CustomIntegerAlbum").get("CustomInteger"));
executor.outFields.put("CustomLong"   , executor.getContextAlbum("CustomLongAlbum"   ).get("CustomLong"   ));
executor.outFields.put("CustomFloat"  , executor.getContextAlbum("CustomFloatAlbum"  ).get("CustomFloat"  ));
executor.outFields.put("CustomDouble" , executor.getContextAlbum("CustomDoubleAlbum" ).get("CustomDouble" ));
executor.outFields.put("CustomString" , executor.getContextAlbum("CustomStringAlbum" ).get("CustomString" ));
executor.outFields.put("CustomALong"  , executor.getContextAlbum("CustomALongAlbum"  ).get("CustomALong"  ));
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
