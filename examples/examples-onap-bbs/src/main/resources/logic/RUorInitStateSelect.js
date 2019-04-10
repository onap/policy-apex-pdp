executor.logger.info("Begin Execution RUorInitStateSelect.js");
executor.logger.info(executor.subject.id);
executor.logger.info(executor.inFields);

var returnValue = executor.isTrue;
var result = null;

var attachmentPoint = executor.inFields.get("attachmentPoint");
var vcpeClosedLoopStatus = executor.getContextAlbum("VCPEClosedLoopStatusAlbum").get(attachmentPoint);

executor.logger.info("==========>" + executor.outFields);
executor.logger.info("==========>" + executor.inFields);

result = vcpeClosedLoopStatus.get("result");

if (result === "SUCCESS") {
    executor.subject.getTaskKey("SdncResourceUpdateTask").copyTo(executor.selectedTask);
} else {
    executor.subject.getTaskKey("ErrorAAIServiceAssignedLogTask").copyTo(executor.selectedTask);
    onsetFlag = executor.isFalse;
} 

executor.logger.info("State Selected Task:" + executor.selectedTask);
executor.logger.info("End Execution RUorInitStateSelect.js");
