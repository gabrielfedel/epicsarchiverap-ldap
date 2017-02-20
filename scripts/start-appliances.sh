#!/bin/bash

## This scripts initializes all four Tomcats required 
## to start EPICS Archiver
## Author: Gustavo CIOTTO PINTON

GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

## Verifies if a given environment variable are set
function isEnvironmentVariableSet {
	
	local  __resultvar=$2
    local  isSet=false
	
	if [ $1 ]; then
		isSet=true
	fi
	
	# 'Return' section
    if [[ "$__resultvar" ]]; then
        eval $__resultvar="'$isSet'"
    else
        echo "$isSet"
    fi
}

function startAppliance {
	
	export CATALINA_BASE=${DEPLOY_DIR}/$1 
	${CATALINA_HOME}/bin/catalina.sh start
}

function startAll {
	
	echo "(i) Checking environment variables..."
	
	echo -n "  - Checking CATALINA_HOME..."
	isEnvironmentVariableSet "${CATALINA_HOME}" result
	
	if [ "$result" = true ]; then
		printf "${GREEN}Ok!${NC}\n"
	else
		printf "${RED}Failed!${NC}\n"
		echo "   - Set CATALINA_HOME to resume..."
		exit 1
	fi
	
	echo -n "  - Checking DEPLOY_DIR..."
	isEnvironmentVariableSet "${DEPLOY_DIR}" result
	
	if [ "$result" = true ]; then
		printf "${GREEN}Ok!${NC}\n"
	else
		printf "${RED}Failed!${NC}\n"
		echo "   - Set DEPLOY_DIR to resume..."
		exit 1
	fi
	
	echo "(ii) Starting appliances..."
	
	printf "${GREEN}Starting MGMT appliance...${NC}\n"
	
	startAppliance "mgmt"
	
	printf "${GREEN}Starting ENGINE appliance...${NC}\n"
	
	startAppliance "engine"
	
	printf "${GREEN}Starting ETL appliance...${NC}\n"
	
	startAppliance "etl"
	
	printf "${GREEN}Starting RETRIEVAL appliance...${NC}\n"
	
	startAppliance "retrieval"
}

