#
# Docker file to build an image that runs APEX on Java 8 in alpine
#
FROM onap/policy-jre-alpine

LABEL maintainer="Policy Team"

ARG POLICY_LOGS=/var/log/onap/policy/apex-pdp
ENV POLICY_HOME=/opt/app/policy/apex-pdp
ENV POLICY_LOGS=$POLICY_LOGS

RUN apk add --no-cache \
        vim \
        iproute2 \
        iputils \
    && addgroup -S apexuser && adduser -S apexuser -G apexuser \
    && mkdir -p $POLICY_HOME \
    && mkdir -p $POLICY_LOGS \
    && chown -R apexuser:apexuser $POLICY_LOGS \
    && mkdir /packages
COPY /maven/apex-pdp-package-full.tar.gz /packages
RUN tar xvfz /packages/apex-pdp-package-full.tar.gz --directory $POLICY_HOME \
    && rm /packages/apex-pdp-package-full.tar.gz \
    && find /opt/app -type d -perm 755 \
    && find /opt/app -type f -perm 644 \
    && chmod 755 $POLICY_HOME/bin/* \
    && cp -pr $POLICY_HOME/examples /home/apexuser \
    && chown -R apexuser:apexuser /home/apexuser/* $POLICY_HOME \
    && chmod 755 $POLICY_HOME/etc/*

USER apexuser
ENV PATH $POLICY_HOME/bin:$PATH
WORKDIR /home/apexuser
