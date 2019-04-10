load("nashorn:mozilla_compat.js");
importClass(org.apache.avro.Schema);

executor.logger.info("Begin Execution NomadicEventSuccess.js");
executor.logger.info(executor.subject.id);
executor.logger.info(executor.inFields);

var attachmentPoint = executor.inFields.get("attachmentPoint");
var vcpeClosedLoopStatus = executor.getContextAlbum("VCPEClosedLoopStatusAlbum").get(attachmentPoint);

executor.logger.info("==========>" + executor.outFields);
executor.logger.info("==========>" + executor.inFields);

result = vcpeClosedLoopStatus.get("result");

if (result === "SUCCESS") {
    returnValue = executor.isTrue;
    executor.outFields.put("result", "SUCCCESS");
    executor.logger.info("BBS policy Execution Done$$$$$$$$$$$");
} else {
    executor.logger.info("BBS policy Execution Failed$$$$$$$$$$$");
    executor.outFields.put("result", "FAILURE");
    returnValue = executor.isFalse;
}

var returnValue = executor.isTrue;
executor.logger.info("End Execution NomadicEventSuccess.js");
