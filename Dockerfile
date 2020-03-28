# Grab the latest wildfly image
FROM jboss/wildfly

# Add our war base64File to the deployment
ADD ./target/blink.war /opt/jboss/wildfly/standalone/deployments/

# Set a health check
HEALTHCHECK --interval=5s \
            --timeout=5s \
            CMD curl -f http://127.0.0.1:8080/blink/api/ping || exit 1
