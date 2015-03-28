#!/bin/bash

gradle clean chitransittracker-alerts:distZip
gradle chitransittracker-routes:distZip
gradle chitransittracker-api:war

mkdir deploy

cp chitransittracker-alerts/build/distributions/chitransittracker-alerts-1.0.zip deploy/
cp chitransittracker-routes/build/distributions/chitransittracker-routes-1.0.zip deploy/
cp chitransittracker-api/build/libs/chitransittracker-api-1.0.war deploy/
