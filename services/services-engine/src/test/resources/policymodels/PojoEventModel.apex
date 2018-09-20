model create name=PojoEventModel version=0.0.1

schema create name=TestPojoType version=0.0.1 flavour=Java schema=org.onap.policy.apex.service.engine.event.testpojos.TestPojo
schema create name=TestPojoListType version=0.0.1 flavour=Java schema=org.onap.policy.apex.service.engine.event.testpojos.TestPojoList

event create name=PojoEvent version=0.0.1 nameSpace=org.onap.policy.apex.service.engine.event.testpojos source=Outside target=Apex

event parameter create name=PojoEvent version=0.0.1 parName=POJO_PAR schemaName=TestPojoType

event create name=PojoListEvent version=0.0.1 nameSpace=org.onap.policy.apex.service.engine.event.testpojos source=Outside target=Apex

event parameter create name=PojoListEvent version=0.0.1 parName=POJO_LIST_PAR schemaName=TestPojoListType

