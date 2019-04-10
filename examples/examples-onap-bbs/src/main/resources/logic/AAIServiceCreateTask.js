load("nashorn:mozilla_compat.js");
importClass(org.apache.avro.Schema);
importClass(java.io.BufferedReader);
importClass(java.io.IOException);
importClass(java.nio.file.Files);
importClass(java.nio.file.Paths);

executor.logger.info("Begin Execution AAIServiceCreateTask.js");
executor.logger.info(executor.subject.id);
executor.logger.info(executor.inFields);

var attachmentPoint = executor.inFields.get("attachmentPoint");
var requestID = executor.inFields.get("requestID");
var serviceInstanceId = executor.inFields.get("serviceInstanceId");

//Get the AAI URL from configuraiotn file
var AAI_URL = "localhost:8080";
var CUSTOMER_ID = requestID;
var BBS_CFS_SERVICE_TYPE = "BBS-CFS-Access_Test";
var SERVICE_INSTANCE_UUID = serviceInstanceId;

try {
    var  br = Files.newBufferedReader(Paths.get("/home/apexuser/examples/config/ONAPBBS/config.txt"));
    // read line by line
    var line;
    while ((line = br.readLine()) != null) {
        if (line.startsWith("AAI_URL")) {
            var str = line.split("=");
            AAI_URL = str[str.length - 1];
            break;
        }

    }
} catch (err) {
    executor.logger.info("Failed to retrieve data " + err);
}
executor.logger.info("AAI_URL=>" + AAI_URL);


var attachmentPoint = executor.inFields.get("attachmentPoint");
var requestID = executor.inFields.get("requestID");
var serviceInstanceId = executor.inFields.get("serviceInstanceId");

var vcpeClosedLoopStatus = executor.getContextAlbum("VCPEClosedLoopStatusAlbum").get(attachmentPoint);
executor.logger.info(vcpeClosedLoopStatus);

var jsonObj = JSON.parse(vcpeClosedLoopStatus.get("aai_message"));

jsonObj['orchestration-status'] = "created";
executor.logger.info(" string" + JSON.stringify(jsonObj, null, 4));

var aaiUpdateResult = true;


/*BBS Policy updates  {{bbs-cfs-service-instance-UUID}} orchestration-status [ assigned --> created ]*/
try {
    //var urlPut =   "https://" + AAI_URL + "/aai/v14/business/customers/customer/" + CUSTOMER_ID + "/service-subscriptions/service-subscription/" + BBS_CFS_SERVICE_TYPE + "/service-instances/service-instance/" + SERVICE_INSTANCE_UUID;
    var urlPut = "http://" + AAI_URL + "/RestConfServer/rest/operations/policy/su2/aaiUpdate";
    result = httpPut(urlPut, JSON.stringify(jsonObj)).data;
    executor.logger.info("Data received From " + urlPut + " " +result.toString());
    repos = JSON.parse(result);
    executor.logger.info("After Parse " + result.toString());

    if (result == "") {
        aaiUpdateResult = false;
    }
}catch (err) {
    executor.logger.info("Failed to retrieve data " + err);
    aaiUpdateResult = false;
}

/* If Success then Fill output schema */
if (aaiUpdateResult === true) {
    vcpeClosedLoopStatus.put("result", "SUCCESS");
} else {
    vcpeClosedLoopStatus.put("result", "FAILURE");
}


executor.outFields.put("requestID", requestID);
executor.outFields.put("attachmentPoint", attachmentPoint);
executor.outFields.put("serviceInstanceId",  executor.inFields.get("serviceInstanceId"));

var returnValue = executor.isTrue;
executor.logger.info("==========>" + executor.outFields);
executor.logger.info("End Execution AAIServiceCreateTask.js");


function httpGet(theUrl){
    var con = new java.net.URL(theUrl).openConnection();
    con.requestMethod = "GET";
    return asResponse(con);
}

function httpPost(theUrl, data, contentType){
    contentType = contentType || "application/json";
    var con = new java.net.URL(theUrl).openConnection();
    con.requestMethod = "POST";
    con.setRequestProperty("Content-Type", contentType);
    con.doOutput=true;
    write(con.outputStream, data);
    return asResponse(con);
}

function httpPut(theUrl, data, contentType){
    contentType = contentType || "application/json";
    var con = new java.net.URL(theUrl).openConnection();
    con.requestMethod = "PUT";
    con.setRequestProperty("Content-Type", contentType);
    con.doOutput=true;
    write(con.outputStream, data);
    return asResponse(con);
}

function asResponse(con){
    var d = read(con.inputStream);
    return {data : d, statusCode : con.resultCode};
}

function write(outputStream, data){
    var wr = new java.io.DataOutputStream(outputStream);
    wr.writeBytes(data);
    wr.flush();
    wr.close();
}

function read(inputStream){
    var inReader = new java.io.BufferedReader(new java.io.InputStreamReader(inputStream));
    var inputLine;
    var result = new java.lang.StringBuffer();

    while ((inputLine = inReader.readLine()) != null) {
           result.append(inputLine);
    }
    inReader.close();
    return result.toString();
}


