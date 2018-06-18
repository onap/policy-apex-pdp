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

#
# Docker file to build an image that runs APEX on Java 8 in Ubuntu
#
# apex/base:0.6.0
FROM ubuntu:16.04
MAINTAINER John Keeney John.Keeney@ericsson.com

RUN apt-get update && \
	apt-get upgrade -y && \
	apt-get install -y software-properties-common && \
	add-apt-repository ppa:webupd8team/java -y && \
	apt-get update && \
	echo oracle-javax8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections && \
	apt-get install -y oracle-java8-installer && \
	rm -rf /var/cache/oracle-jdk8-installer && \
	apt-get clean

RUN mkdir /packages
COPY apex-pdp-package-full-2.0.0-SNAPSHOT.deb /packages
RUN dpkg -i packages/apex-pdp-package-full-2.0.0-SNAPSHOT.deb  && \
	rm /packages/apex-pdp-package-full-2.0.0-SNAPSHOT.deb

ENV PATH /opt/ericsson/apex/apex/bin:$PATH

RUN  apt-get clean

RUN chown -R apexuser:apexuser /home/apexuser/*
WORKDIR /home/apexuser
