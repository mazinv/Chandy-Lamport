#!/bin/bash

mvn clean install -DskipTest
vagrant up

curl 192.168.10.10:4567/marker
curl 192.168.10.11:4567/marker
curl 192.168.10.12:4567/marker
curl 192.168.10.13:4567/marker
