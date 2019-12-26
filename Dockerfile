FROM jboss/wildfly
ADD ./target/blink.war /opt/jboss/wildfly/standalone/deployments/

# Testing CI