load("nashorn:mozilla_compat.js");
importClass(java.io.BufferedReader);
importClass(java.io.IOException);
importClass(java.nio.file.Files);
importClass(java.nio.file.Paths);
executor.logger.info("Begin Execution ServiceUpdateStateCpeAuthTask.js");
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
var serviceInstanceId = null;
var AAI_URL = "localhost:8080";
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

if (clEvent.getAai().get("attachmentPoint") != null) {
   attachmentPoint = clEvent.getAai().get("attachmentPoint");
   executor.logger.info("attachmentPoint = " + attachmentPoint);
   serviceInstanceId = clEvent.getAai().get("service-information.hsia-cfs-service-instance-id");
   executor.logger.info("serviceInstanceId = " + serviceInstanceId);

   var result;
   var jsonObj;
   var prevResult = true;


   /*BBS Policy updates  {{bbs-cfs-service-instance-UUID}} orchestration-status [ assigned --> created ]*/
   var json = {id: 1, someValue: "1234"};
   try {
       var urlPut = "http://" + AAI_URL + "/RestConfServer/rest/operations/policy/cpe/cpeAuthUpdate";
       result = httpPut(urlPut, JSON.stringify(json)).data;
       executor.logger.info("Data received From " + urlPut + " " +result.toString());
       repos = JSON.parse(result);
       executor.logger.info("After Parse " + result.toString());

       if (result == "") {
           prevResult = false;
       }
   }catch (err) {
       executor.logger.info("Failed to retrieve data " + err);
       prevResult = false;
   }

   /* If Success then Fill output schema */
   if (prevResult == true) {
       executor.outFields.put("result", "SUCCCESS");
   } else {
       executor.outFields.put("result", "FAILURE");
   }

   executor.outFields.put("requestID", requestID);
   executor.outFields.put("attachmentPoint", attachmentPoint);
   executor.outFields.put("serviceInstanceId", attachmentPoint);
   executor.logger.info(executor.outFields);
}



var returnValue = executor.isTrue;
executor.logger.info("End Execution ServiceUpdateStateCpeAuthTask.js");


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
