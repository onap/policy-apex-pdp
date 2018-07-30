/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */
 
 The examples-servlet application demonstrate the capability that apex-pdp can be simply started as a servlet deployed in any application server (example - tomcat).
 
 The examples-servlet war file can be found at following locations.
 
 1) Inside a container where apex-pdp is installed - /opt/app/policy/apex-pdp/apex-pdp-package-full-${project.version}/war
 2) Inside the tarball.gz distribution of apex-pdp - /war
 
 
 Kindly follow the following steps to run apex-pdp servlet example war file.
 
 1) Install a tomcat server (or any other web server of your choice).
 2) Goto webapps folder under tomcat installation directory.
 3) Copy the examples-servlet war file here.
 4) Make sure you have given correct access permission to the war file to execute.
 5) Goto bin directory of tomcat and start the server using appropriate script for windows/linux.
 6) Open a browser window and type the following url - http://localhost:8080/examples-servlet-2.0.0-SNAPSHOT/apex/eventInput/Status
 7) If everything is fine, you must see the following JSON in browser.
    {
     "INPUTS": "[FirstConsumer, SecondConsumer]",
     "STAT": 1,
     "POST": 0,
     "PUT":  0
    }
 8) Open a rest client (example - Postman, ARC etc.) and make a http post request with following details.
    Method: POST
    URL: http://localhost:8080/examples-servlet-2.0.0-SNAPSHOT/apex/FirstConsumer/EventIn
    Header: [{"key":"Content-Type","value":"application/json"}]
    Body: {
            "nameSpace": "org.onap.policy.apex.sample.events",
            "name": "Event0100",
            "version": "0.0.1",
            "source": "test",
            "target": "apex",
            "TestSlogan": "Test slogan for External Event1",
            "TestMatchCase": 0,
            "TestTimestamp": 1469781869268,
            "TestTemperature": 8071.559
          }
 9) If everything is fine, you must see the following response from server in your rest client.
 	Status: 200 OK
 	Body: {
            "name": "Event0104",
            "version": "0.0.1",
            "nameSpace": "org.onap.policy.apex.sample.events",
            "source": "Act",
            "target": "Outside",
            "TestActCaseSelected": 2,
            "TestActStateTime": 1532960255194,
            "TestDecideCaseSelected": 1,
            "TestDecideStateTime": 1532960255114,
            "TestEstablishCaseSelected": 3,
            "TestEstablishStateTime": 1532960255085,
            "TestMatchCase": 0,
            "TestMatchCaseSelected": 1,
            "TestMatchStateTime": 1532960255047,
            "TestSlogan": "Test slogan for External Event1",
            "TestTemperature": 8071.559,
            "TestTimestamp": 1469781869268
          }
 10) Congratulations!!! you have successfully run examples-servlet application.
 11) Try playing more by changing the json body content in http post request (Step-8) and fetching status from browser (Step-6).
 
 
 Note - If not working, please check the IP, PORT & webapp context root (examples-servlet-2.0.0-SNAPSHOT) as it may be different.
    