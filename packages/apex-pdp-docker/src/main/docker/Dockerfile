#
# Docker file to build an image that runs APEX on Java 8 in Ubuntu
#
FROM ubuntu:16.04

RUN apt-get update && \
        apt-get upgrade -y && \
        apt-get install -y software-properties-common && \
        add-apt-repository ppa:webupd8team/java -y && \
        apt-get update && \
        apt-get install -y openjdk-8-jdk

# Create apex user and group
RUN groupadd apexuser
RUN useradd --create-home -g apexuser apexuser

# Add Apex-specific directories and set ownership as the Apex admin user
RUN mkdir -p /opt/app/policy/apex-pdp
RUN mkdir -p /var/log/onap/policy/apex-pdp
RUN chown -R apexuser:apexuser /var/log/onap/policy/apex-pdp

# Unpack the tarball
RUN mkdir /packages
COPY apex-pdp-package-full.tar.gz /packages
RUN tar xvfz /packages/apex-pdp-package-full.tar.gz --directory /opt/app/policy/apex-pdp
RUN rm /packages/apex-pdp-package-full.tar.gz

# Ensure everything has the correct permissions
RUN find /opt/app -type d -perm 755
RUN find /opt/app -type f -perm 644
RUN chmod a+x /opt/app/policy/apex-pdp/bin/*

# Copy examples to Apex user area
RUN cp -pr /opt/app/policy/apex-pdp/examples /home/apexuser

ENV PATH /opt/app/policy/apex-pdp/bin:$PATH

RUN apt-get clean

RUN chown -R apexuser:apexuser /home/apexuser/*
WORKDIR /home/apexuser
