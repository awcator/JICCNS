#!/bin/bash
figlet  "Jiccns   v0.1"
INSTALL_LOCATION="/opt/jiccns/"
java -Xss100M -Xmx4g -Xms2g -Duser.dir="`pwd`" -jar /opt/jiccns/JICCNS.jar "$@"