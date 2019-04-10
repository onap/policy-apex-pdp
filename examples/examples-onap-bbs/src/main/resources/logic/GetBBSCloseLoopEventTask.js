load("nashorn:mozilla_compat.js");

executor.logger.info("Begin Execution GetBBSCloseLoopEventTask.js");
executor.logger.info(executor.subject.id);
executor.logger.info(executor.inFields);
var returnValue = executor.isTrue;

//**************************************************************************//

var clEventType = Java.type("org.onap.policy.controlloop.VirtualControlLoopEvent");
var clEvent = executor.inFields.get("VirtualControlLoopEvent");
executor.logger.info(clEvent.toString());
executor.logger.info(clEvent.getClosedLoopControlName());

var requestID = clEvent.getRequestId();
executor.logger.info("requestID = " + requestID);
var attachmentPoint = null;
var vcpeClosedLoopStatus = null;
var serviceInstanceId = null;

if (clEvent.getAai().get("attachmentPoint") != null) {
   attachmentPoint = clEvent.getAai().get("attachmentPoint");
   executor.logger.info("attachmentPoint = " + attachmentPoint);
   vcpeClosedLoopStatus = executor.getContextAlbum("VCPEClosedLoopStatusAlbum").get(attachmentPoint);
   serviceInstanceId = clEvent.getAai().get("service-information.hsia-cfs-service-instance-id");
   executor.logger.info("serviceInstanceId = " + serviceInstanceId);

   if (vcpeClosedLoopStatus == null) {
      executor.logger.info("Creating context information for new ONT Device \"" + attachmentPoint.toString() + "\"");

      vcpeClosedLoopStatus = executor.getContextAlbum("VCPEClosedLoopStatusAlbum").getSchemaHelper().createNewInstance();

      vcpeClosedLoopStatus.put("closedLoopControlName", clEvent.getClosedLoopControlName());
      vcpeClosedLoopStatus.put("closedLoopAlarmStart",  clEvent.getClosedLoopAlarmStart().toEpochMilli());
      vcpeClosedLoopStatus.put("closedLoopEventClient", clEvent.getClosedLoopEventClient());
      vcpeClosedLoopStatus.put("closedLoopEventStatus", clEvent.getClosedLoopEventStatus().toString());
      vcpeClosedLoopStatus.put("version",               clEvent.getVersion());
      vcpeClosedLoopStatus.put("requestID",             clEvent.getRequestId().toString());
      vcpeClosedLoopStatus.put("target_type",           clEvent.getTargetType().toString());
      vcpeClosedLoopStatus.put("target",                clEvent.getTarget());
      vcpeClosedLoopStatus.put("from",                  clEvent.getFrom());
      vcpeClosedLoopStatus.put("policyScope",           "Nomadic ONT");
      vcpeClosedLoopStatus.put("policyName",            clEvent.getPolicyName());
      vcpeClosedLoopStatus.put("policyVersion",         "1.0.0");
      vcpeClosedLoopStatus.put("notificationTime",      java.lang.System.currentTimeMillis());
      vcpeClosedLoopStatus.put("message",               "");
      vcpeClosedLoopStatus.put("result",               "SUCCESS");
      var aaiInfo = executor.getContextAlbum("VCPEClosedLoopStatusAlbum").getSchemaHelper().createNewSubInstance("VCPE_AAI_Type");

      aaiInfo.put("attachmentPoint",      clEvent.getAai().get("attachmentPoint"));
      aaiInfo.put("cvlan",              clEvent.getAai().get("cvlan"));
      aaiInfo.put("service_information_hsia_cfs_service_instance_id", clEvent.getAai().get("service-information.hsia-cfs-service-instance-id"));
      aaiInfo.put("svlan", clEvent.getAai().get("svlan"));
      aaiInfo.put("remoteId",  clEvent.getAai().get("remoteId"));


      vcpeClosedLoopStatus.put("AAI", aaiInfo);

      if (clEvent.getClosedLoopAlarmEnd() != null) {
         vcpeClosedLoopStatus.put("closedLoopAlarmEnd", clEvent.getClosedLoopAlarmEnd().toEpochMilli());
      } else {
         vcpeClosedLoopStatus.put("closedLoopAlarmEnd", java.lang.Long.valueOf(0));
      }

      executor.getContextAlbum("VCPEClosedLoopStatusAlbum").put(attachmentPoint.toString(), vcpeClosedLoopStatus);
      executor.logger.info("Created context information for new vCPE VNF \"" + attachmentPoint.toString() + "\"");
   }

   executor.outFields.put("requestID", requestID);
   executor.outFields.put("attachmentPoint", attachmentPoint);
   executor.outFields.put("serviceInstanceId", attachmentPoint);
   executor.logger.info(executor.outFields);
}

executor.logger.info("********************* Event Successfully Received and stored in album *********************");

//**************************************************************************//


