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

import http.client
import json
import time
from kafka import KafkaConsumer, KafkaProducer
  
class StaticFlowPusher(object):
  
    def __init__(self, server):
        self.server = server
  
    def get(self, data):
        ret = self.rest_call({}, 'GET')
        return json.loads(ret[2])
  
    def set(self, data):
        ret = self.rest_call(data, 'POST')
        return ret[0] == 200
  
    def remove(self, objtype, data):
        ret = self.rest_call(data, 'DELETE')
        return ret[0] == 200
  
    def getControllerSummary(self, data):
        ret = self.rest_call_controller_summary({}, 'GET')
        return json.loads(ret[2])
  
    def getLinks(self, data):
        ret = self.rest_call_links({}, 'GET')
        return json.loads(ret[2].decode())
  
    def rest_call(self, data, action):
        path = '/wm/staticflowpusher/json'
        headers = {
            'Content-type': 'application/json',
            'Accept': 'application/json',
            }
        body = json.dumps(data)
        conn = http.client.HTTPConnection(self.server, 8080)
        conn.request(action, path, body, headers)
        response = conn.getresponse()
        ret = (response.status, response.reason, response.read())
        print(ret)
        conn.close()
        return ret
  
    def rest_call_controller_summary(self, data, action):
        path = '/wm/core/controller/summary/json'
        headers = {
            'Content-type': 'application/json',
            'Accept': 'application/json',
            }
        body = json.dumps(data)
        conn = http.client.HTTPConnection(self.server, 8080)
        conn.request(action, path, body, headers)
        response = conn.getresponse()
        ret = (response.status, response.reason, response.read())
        print(ret)
        conn.close()
        return ret

    def rest_call_links(self, data, action):
        path = '/wm/topology/links/json'
        headers = {
            'Content-type': 'application/json',
            'Accept': 'application/json',
            }
        body = json.dumps(data)
        conn = http.client.HTTPConnection(self.server, 8080)
        conn.request(action, path, body, headers)
        response = conn.getresponse()
        ret = (response.status, response.reason, response.read())
        conn.close()
        return ret
  
pusher = StaticFlowPusher('127.0.1.1')


def parseLinks(links):
	#print("\n\n\n",links)
	result = []
	for link in links:
		list = []
		#print("\n\n\n",link)
		#print("\nsrc-switch : s", link['src-switch'][len(link['src-switch'])-1])
		#print("\ndst-switch : s", link['dst-switch'][len(link['dst-switch'])-1])
		list.append("s")
		list.append(link['src-switch'][len(link['src-switch'])-1])
		list.append("-s")
		list.append(link['dst-switch'][len(link['dst-switch'])-1])
		result.append(''.join(list))
	#print(result, "\n")
	return result



counter =0
healthyList = []
testableList = []
healthyLinks = ""
testableLinks = ""
producer = KafkaProducer(bootstrap_servers='localhost:9092')
while(True):
	time.sleep(30)
	switchLinks = pusher.getLinks({})
	if counter == 0:
		healthyList = parseLinks(switchLinks)
		#Build All Links
		print("READING LINKS FROM MININET\n")
		for l in healthyList:
			link = ""
			#print(l, "\n")
			#Links between switches [s6-s7 is ignored so it matches VPN SCENARIO]
			if(l == "s1-s5"):
				link = "{'nameSpace': 'org.onap.policy.apex.examples.pcvs.vpnsla','name': 'EdgeContextEventIn','version': '1.0.0','source': 'LinkMonitor.py','target': 'VpnSlaPolicy_EdgeContext','status': true,'edgeName': 'L05', 'start': 'A1CO','end': 'BBL'}"
				producer.send("apex-in-0", bytes(link, encoding="ascii"))
			if(l == "s5-s6"):
				link = "{'nameSpace': 'org.onap.policy.apex.examples.pcvs.vpnsla','name': 'EdgeContextEventIn','version': '1.0.0','source': 'LinkMonitor.py','target': 'VpnSlaPolicy_EdgeContext','status': true,'edgeName': 'L09', 'start': 'BBL','end': 'BBR'}"
				producer.send("apex-in-0", bytes(link, encoding="ascii"))
			if(l == "s2-s6"):
				link = "{'nameSpace': 'org.onap.policy.apex.examples.pcvs.vpnsla','name': 'EdgeContextEventIn','version': '1.0.0','source': 'LinkMonitor.py','target': 'VpnSlaPolicy_EdgeContext','status': true,'edgeName': 'L07', 'start': 'A2CO','end': 'BBR'}"
				producer.send("apex-in-0", bytes(link, encoding="ascii"))
			if(l == "s5-s7"):
				link = "{'nameSpace': 'org.onap.policy.apex.examples.pcvs.vpnsla','name': 'EdgeContextEventIn','version': '1.0.0','source': 'LinkMonitor.py','target': 'VpnSlaPolicy_EdgeContext','status': true,'edgeName': 'L10', 'start': 'BBR','end': 'BBL'}"
				producer.send("apex-in-0", bytes(link, encoding="ascii"))
			if(l == "s3-s5"):
				link = "{'nameSpace': 'org.onap.policy.apex.examples.pcvs.vpnsla','name': 'EdgeContextEventIn','version': '1.0.0','source': 'LinkMonitor.py','target': 'VpnSlaPolicy_EdgeContext','status': true,'edgeName': 'L06', 'start': 'B1CO','end': 'BBL'}"
				producer.send("apex-in-0", bytes(link, encoding="ascii"))
			if(l == "s4-s6"):
				link = "{'nameSpace': 'org.onap.policy.apex.examples.pcvs.vpnsla','name': 'EdgeContextEventIn','version': '1.0.0','source': 'LinkMonitor.py','target': 'VpnSlaPolicy_EdgeContext','status': true,'edgeName': 'L08', 'start': 'B2CO','end': 'BBR'}"
				producer.send("apex-in-0", bytes(link, encoding="ascii"))
		#Links between switches and hosts [NoT SENT IN FROM FLOODLIGHT]
		producer.send("apex-in-0", b"{'nameSpace': 'org.onap.policy.apex.examples.pcvs.vpnsla','name': 'EdgeContextEventIn','version': '1.0.0','source': 'LinkMonitor.py','target': 'VpnSlaPolicy_EdgeContext','status': true,'edgeName': 'L01', 'start': 'A1','end': 'A1CO'}")
		producer.send("apex-in-0", b"{'nameSpace': 'org.onap.policy.apex.examples.pcvs.vpnsla','name': 'EdgeContextEventIn','version': '1.0.0','source': 'LinkMonitor.py','target': 'VpnSlaPolicy_EdgeContext','status': true,'edgeName': 'L02', 'start': 'B1','end': 'B1CO'}")
		producer.send("apex-in-0", b"{'nameSpace': 'org.onap.policy.apex.examples.pcvs.vpnsla','name': 'EdgeContextEventIn','version': '1.0.0','source': 'LinkMonitor.py','target': 'VpnSlaPolicy_EdgeContext','status': true,'edgeName': 'L03', 'start': 'A2','end': 'A2CO'}")
		producer.send("apex-in-0", b"{'nameSpace': 'org.onap.policy.apex.examples.pcvs.vpnsla','name': 'EdgeContextEventIn','version': '1.0.0','source': 'LinkMonitor.py','target': 'VpnSlaPolicy_EdgeContext','status': true,'edgeName': 'L04', 'start': 'B2','end': 'B2CO'}")
		print("LINKS HAVE BEEN SENT TO APEX\n")

		#Build Customers
		print("BUILDING CUSTOMERS\n")
		producer.send("apex-in-0", b"{'nameSpace': 'org.onap.policy.apex.examples.pcvs.vpnsla','name': 'CustomerContextEventIn','version': '1.0.0','source': 'LinkMonitor.py','target': 'VpnSlaPolicy_CustomerContext','dtYTD': 10,'dtSLA': 180,'links': 'L01 L05 L09 L10','customerName': 'A', 'priority': true,'satisfaction': 80}")
		producer.send("apex-in-0", b"{'nameSpace': 'org.onap.policy.apex.examples.pcvs.vpnsla','name': 'CustomerContextEventIn','version': '1.0.0','source': 'LinkMonitor.py','target': 'VpnSlaPolicy_CustomerContext','dtYTD': 120,'dtSLA': 180,'links': 'L02 L07 L09 L10','customerName': 'B', 'priority': false,'satisfaction': 99}")
		print("CUSTOMERS HAVE BEEN SENT TO APEX\n")
		healthyLinks = switchLinks
		myfile = open('LinkInfo.json', 'a')
		myfile.write(str(healthyLinks))
		myfile.write('\n')
		myfile.close()
		print("We start off with", len(healthyLinks), "healthy Links!\n")
	else:
		testableList = parseLinks(switchLinks)
		issueLink = "";
		for h in healthyList:
			issueLink = h
			for t in testableList:
				if t == h:
					issueLink = ""
			if issueLink != "":
				print("There is an issue with the links! ", issueLink, " \n")
				if(issueLink == "s1-s5"):
					link = "{'nameSpace': 'org.onap.policy.apex.examples.pcvs.vpnsla','name': 'VpnSlaTrigger','version': '1.0.0','source': 'LinkMonitor.py','target': 'VpnSlaPolicy','status': 'DOWN','edgeName': 'L05'}"
					producer.send("apex-in-0", bytes(link, encoding="ascii"))
				if(issueLink == "s5-s6"):
					link = "{'nameSpace': 'org.onap.policy.apex.examples.pcvs.vpnsla','name': 'VpnSlaTrigger','version': '1.0.0','source': 'LinkMonitor.py','target': 'VpnSlaPolicy','status': 'DOWN','edgeName': 'L09'}"
					producer.send("apex-in-0", bytes(link, encoding="ascii"))
				if(issueLink == "s2-s6"):
					link = "{'nameSpace': 'org.onap.policy.apex.examples.pcvs.vpnsla','name': 'VpnSlaTrigger','version': '1.0.0','source': 'LinkMonitor.py','target': 'VpnSlaPolicy','status': 'DOWN','edgeName': 'L07'}"
					producer.send("apex-in-0", bytes(link, encoding="ascii"))
				if(issueLink == "s5-s7"):
					link = "{'nameSpace': 'org.onap.policy.apex.examples.pcvs.vpnsla','name': 'VpnSlaTrigger','version': '1.0.0','source': 'LinkMonitor.py','target': 'VpnSlaPolicy','status': 'DOWN','edgeName': 'L10'}"
					producer.send("apex-in-0", bytes(link, encoding="ascii"))
				if(issueLink == "s3-s5"):
					link = "{'nameSpace': 'org.onap.policy.apex.examples.pcvs.vpnsla','name': 'VpnSlaTrigger','version': '1.0.0','source': 'LinkMonitor.py','target': 'VpnSlaPolicy','status': 'DOWN','edgeName': 'L06'}"
					producer.send("apex-in-0", bytes(link, encoding="ascii"))
				if(issueLink == "s4-s6"):
					link = "{'nameSpace': 'org.onap.policy.apex.examples.pcvs.vpnsla','name': 'VpnSlaTrigger','version': '1.0.0','source': 'LinkMonitor.py','target': 'VpnSlaPolicy','status': 'DOWN','edgeName': 'L08'}"
					producer.send("apex-in-0", bytes(link, encoding="ascii"))
				break
		if issueLink == "":
			print("All Links are working\n")
			producer.send("apex-in-0", b"{'nameSpace': 'org.onap.policy.apex.examples.pcvs.vpnsla','name': 'VpnSlaTrigger','version': '1.0.0','source': 'LinkMonitor.py','target': 'VpnSlaPolicy','status': 'UP','edgeName': 'L01'}")
			producer.send("apex-in-0", b"{'nameSpace': 'org.onap.policy.apex.examples.pcvs.vpnsla','name': 'VpnSlaTrigger','version': '1.0.0','source': 'LinkMonitor.py','target': 'VpnSlaPolicy','status': 'UP','edgeName': 'L02'}")
			producer.send("apex-in-0", b"{'nameSpace': 'org.onap.policy.apex.examples.pcvs.vpnsla','name': 'VpnSlaTrigger','version': '1.0.0','source': 'LinkMonitor.py','target': 'VpnSlaPolicy','status': 'UP','edgeName': 'L03'}")
			producer.send("apex-in-0", b"{'nameSpace': 'org.onap.policy.apex.examples.pcvs.vpnsla','name': 'VpnSlaTrigger','version': '1.0.0','source': 'LinkMonitor.py','target': 'VpnSlaPolicy','status': 'UP','edgeName': 'L04'}")
			producer.send("apex-in-0", b"{'nameSpace': 'org.onap.policy.apex.examples.pcvs.vpnsla','name': 'VpnSlaTrigger','version': '1.0.0','source': 'LinkMonitor.py','target': 'VpnSlaPolicy','status': 'UP','edgeName': 'L05'}")
			producer.send("apex-in-0", b"{'nameSpace': 'org.onap.policy.apex.examples.pcvs.vpnsla','name': 'VpnSlaTrigger','version': '1.0.0','source': 'LinkMonitor.py','target': 'VpnSlaPolicy','status': 'UP','edgeName': 'L06'}")
			producer.send("apex-in-0", b"{'nameSpace': 'org.onap.policy.apex.examples.pcvs.vpnsla','name': 'VpnSlaTrigger','version': '1.0.0','source': 'LinkMonitor.py','target': 'VpnSlaPolicy','status': 'UP','edgeName': 'L07'}")
			producer.send("apex-in-0", b"{'nameSpace': 'org.onap.policy.apex.examples.pcvs.vpnsla','name': 'VpnSlaTrigger','version': '1.0.0','source': 'LinkMonitor.py','target': 'VpnSlaPolicy','status': 'UP','edgeName': 'L08'}")
			producer.send("apex-in-0", b"{'nameSpace': 'org.onap.policy.apex.examples.pcvs.vpnsla','name': 'VpnSlaTrigger','version': '1.0.0','source': 'LinkMonitor.py','target': 'VpnSlaPolicy','status': 'UP','edgeName': 'L09'}")
			producer.send("apex-in-0", b"{'nameSpace': 'org.onap.policy.apex.examples.pcvs.vpnsla','name': 'VpnSlaTrigger','version': '1.0.0','source': 'LinkMonitor.py','target': 'VpnSlaPolicy','status': 'UP','edgeName': 'L10'}")
		
		testableLinks = switchLinks
		myfile = open('LinkInfo.json', 'a')
		myfile.write(str(testableLinks))
		myfile.write('\n')
		myfile.close()
	counter += 1
