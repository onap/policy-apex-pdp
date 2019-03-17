#
# Docker file to build an image that runs APEX on Java 8 in alpine
#
FROM alpine:3.9

RUN apk add --no-cache --update bash nss procps coreutils findutils grep zip unzip \
                                     curl wget openssh iproute2 iputils vim openjdk8

# Create apex user and group
RUN addgroup -S apexuser && adduser -S apexuser -G apexuser


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

RUN chown -R apexuser:apexuser /home/apexuser/*

USER apexuser
ENV PATH /opt/app/policy/apex-pdp/bin:$PATH
WORKDIR /home/apexuser
