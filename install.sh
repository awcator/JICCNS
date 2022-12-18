#!/bin/bash
figlet  "Jiccns    Installer"
INSTALL_LOCATION="/opt/jiccns/"
echo -e "Uninstalling older files"
rm -rvf $INSTALL_LOCATION
rm -rvf /usr/bin/jiccns.sh

echo -e "\n\nInstalling"
mkdir -p $INSTALL_LOCATION/JICCNS_simulator/
cp -vr out/artifacts/JICCNS_jar/* $INSTALL_LOCATION
cp -vr ./JICCNS_simulator/res $INSTALL_LOCATION/JICCNS_simulator/
cp jiccns.sh /usr/bin/
echo "Done"