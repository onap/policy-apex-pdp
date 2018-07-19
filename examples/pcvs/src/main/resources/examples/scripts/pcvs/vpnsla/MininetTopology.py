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

#Add Mininet to PATH
import sys
sys.path.insert(0, "/~/mininet")

#Kafka
import httplib
import json
import time
from kafka import KafkaConsumer, KafkaProducer

#Mininet
from mininet.clean import *
from mininet.cli import *
from mininet.link import *
from mininet.log import *
from mininet.net import *
from mininet.node import *
from mininet.nodelib import *
from mininet.topo import *
from mininet.topolib import *

class StaticFlowPusher(object):
	def __init__(self, server):
		self.server = server

	def enableFirewall(self, data):
		path = "/wm/firewall/module/enable/json"
		headers = {'Content-Type': 'application/json','Accept': 'application/json',}
		body = json.dumps(data)
		conn = httplib.HTTPConnection(self.server, 8080)
		conn.request("PUT", path, "")
		response = conn.getresponse()
		ret = (response.status, response.reason, response.read())
		conn.close()
		return ret

	def addRule(self, data):
		path = '/wm/firewall/rules/json'
		headers = {'Content-Type': 'application/json','Accept': 'application/json',}
		body = json.dumps(data)
		conn = httplib.HTTPConnection(self.server, 8080)
		conn.request('POST', path, body, headers)
		response = conn.getresponse()
		ret = (response.status, response.reason, response.read())
		conn.close()
		return ret

	def deleteRule(self, data):
		path = '/wm/firewall/rules/json'
		headers = {'Content-Type': 'application/json','Accept': 'application/json',}
		body = json.dumps(data)
		conn = httplib.HTTPConnection(self.server, 8080)
		conn.request('DELETE', path, body, headers)
		response = conn.getresponse()
		ret = (response.status, response.reason, response.read())
		conn.close()
		return ret

#Build Pusher(REST/IN)
pusher = StaticFlowPusher('127.0.0.1')

net = Mininet(link=TCLink)

#Create Customers
customerA1 = net.addHost( 'A1' )
customerA2 = net.addHost( 'A2' )
customerB1 = net.addHost( 'B1' )
customerB2 = net.addHost( 'B2' )

#Create Switches
switchA1CO = net.addSwitch( 's1' )
switchA2CO = net.addSwitch( 's2' )
switchB1CO = net.addSwitch( 's3' )
switchB2CO = net.addSwitch( 's4' )
switchBBL = net.addSwitch( 's5' )
switchBBR = net.addSwitch( 's6' )
# we need an extra switch here because Mininet does not allow two links between two switches
switchEx = net.addSwitch( 's7' )

#Create Links
net.addLink( customerA1, switchA1CO )
net.addLink( customerA2, switchA2CO )
net.addLink( customerB1, switchB1CO )
net.addLink( customerB2, switchB2CO )
net.addLink( switchA1CO, switchBBL )
net.addLink( switchB1CO, switchBBL )
net.addLink( switchA2CO, switchBBR )
net.addLink( switchB2CO, switchBBR )
net.addLink( switchBBL, switchBBR)
net.addLink( switchBBR, switchEx, bw=1.2 )
net.addLink( switchEx, switchBBL )

#Create Controller
floodlightController = net.addController(name='c0' , controller=RemoteController , ip='127.0.0.1', port=6653)

net.start()

if pusher.enableFirewall({})[0] == 200:
	print("Firewall enabled!")

#print(pusher.addRule({"switchid": "00:00:00:00:00:00:00:01"})[2])
s1id = json.loads(pusher.addRule({"switchid": "00:00:00:00:00:00:00:01"})[2])['rule-id']
s2id = json.loads(pusher.addRule({"switchid": "00:00:00:00:00:00:00:02"})[2])['rule-id']
s3id = json.loads(pusher.addRule({"switchid": "00:00:00:00:00:00:00:03"})[2])['rule-id']
s4id = json.loads(pusher.addRule({"switchid": "00:00:00:00:00:00:00:04"})[2])['rule-id']
s5id = json.loads(pusher.addRule({"switchid": "00:00:00:00:00:00:00:05"})[2])['rule-id']
s6id = json.loads(pusher.addRule({"switchid": "00:00:00:00:00:00:00:06"})[2])['rule-id']
s7id = json.loads(pusher.addRule({"switchid": "00:00:00:00:00:00:00:07"})[2])['rule-id']


result = 100
while result!=0:
	result = net.pingAll(None)
print("Network Simulation Complete")

#Assume control and when finished "exit"
cli = CLI( net )

consumer = KafkaConsumer(bootstrap_servers='localhost:9092',auto_offset_reset='latest')
consumer.subscribe(['apex-out'])
print("Starting Message Loop")
for message in consumer:
	myOutput = json.loads(message.value.decode())
	action = ""
	try:
		print("Checking Message")
		#print("SWITCHES= ",net.switches)
		#print("LINKS= ",net.links)
		#print("VALUES= ",net.values)
		if myOutput['edgeName'] != '':
			print("Message Received: ",myOutput['edgeName'])
			pusher.deleteRule({"ruleid": s1id})
			pusher.deleteRule({"ruleid": s2id})
			pusher.deleteRule({"ruleid": s3id})
			pusher.deleteRule({"ruleid": s4id})
			pusher.deleteRule({"ruleid": s5id})
			pusher.deleteRule({"ruleid": s6id})
			pusher.deleteRule({"ruleid": s7id})
			s1id = json.loads(pusher.addRule({"switchid": "00:00:00:00:00:00:00:01"})[2])['rule-id']
			s2id = json.loads(pusher.addRule({"switchid": "00:00:00:00:00:00:00:02"})[2])['rule-id']
			s3id = json.loads(pusher.addRule({"switchid": "00:00:00:00:00:00:00:03"})[2])['rule-id']
			s4id = json.loads(pusher.addRule({"switchid": "00:00:00:00:00:00:00:04"})[2])['rule-id']
			s5id = json.loads(pusher.addRule({"switchid": "00:00:00:00:00:00:00:05"})[2])['rule-id']
			s6id = json.loads(pusher.addRule({"switchid": "00:00:00:00:00:00:00:06"})[2])['rule-id']
			s7id = json.loads(pusher.addRule({"switchid": "00:00:00:00:00:00:00:07"})[2])['rule-id']
			if myOutput['edgeName'] == "L01":
				action = "link s1 s5 down"
				#net.configLinkStatus('s1', 's5', "down")
				pusher.deleteRule({"ruleid": s1id})
				s1id = json.loads(pusher.addRule({"switchid": "00:00:00:00:00:00:00:01", "action": "DENY"})[2])['rule-id']
			if myOutput['edgeName'] == "L02":
				action = "link s3 s5 down"
				#net.configLinkStatus('s3', 's5', "down")
				pusher.deleteRule({"ruleid": s3id})
				s3id = json.loads(pusher.addRule({"switchid": "00:00:00:00:00:00:00:03", "action": "DENY"})[2])['rule-id']
			if myOutput['edgeName'] == "L03":
				action = "link s2 s6 down"
				#net.configLinkStatus('s2', 's6', "down")
				pusher.deleteRule({"ruleid": s1id})
				s1id = json.loads(pusher.addRule({"switchid": "00:00:00:00:00:00:00:01", "action": "DENY"})[2])['rule-id']
			if myOutput['edgeName'] == "L04":
				action = "link s4 s6 down"
				#net.configLinkStatus('s4', 's6', "down")
				pusher.deleteRule({"ruleid": s3id})
				s3id = json.loads(pusher.addRule({"switchid": "00:00:00:00:00:00:00:03", "action": "DENY"})[2])['rule-id']
			if myOutput['edgeName'] == "L05":
				action = "link s1 s5 down"
				#net.configLinkStatus('s1', 's5', "down")
				pusher.deleteRule({"ruleid": s1id})
				s1id = json.loads(pusher.addRule({"switchid": "00:00:00:00:00:00:00:01", "action": "DENY"})[2])['rule-id']
			if myOutput['edgeName'] == "L06":
				action = "link s3 s5 down"
				#net.configLinkStatus('s3', 's5', "down")
				pusher.deleteRule({"ruleid": s3id})
				s3id = json.loads(pusher.addRule({"switchid": "00:00:00:00:00:00:00:03", "action": "DENY"})[2])['rule-id']
			if myOutput['edgeName'] == "L07":
				action = "link s2 s6 down"
				#net.configLinkStatus('s2', 's6', "down")
				pusher.deleteRule({"ruleid": s2id})
				s2id = json.loads(pusher.addRule({"switchid": "00:00:00:00:00:00:00:02", "action": "DENY"})[2])['rule-id']
			if myOutput['edgeName'] == "L08":
				action = "link s4 s6 down"
				#net.configLinkStatus('s4', 's6', "down")
				pusher.deleteRule({"ruleid": s4id})
				s4id = json.loads(pusher.addRule({"switchid": "00:00:00:00:00:00:00:04", "action": "DENY"})[2])['rule-id']
			if myOutput['edgeName'] == "L09":
				action = "link s5 s6 down"
				#net.configLinkStatus('s5', 's6', "down")
				pusher.deleteRule({"ruleid": s7id})
				s7id = json.loads(pusher.addRule({"switchid": "00:00:00:00:00:00:00:07", "action": "DENY"})[2])['rule-id']
			if myOutput['edgeName'] == "L10":
				print("L10")
			#print(action)
		#print("3")
	except KeyError:
		print(myOutput)
print("HA")
net.stop()
