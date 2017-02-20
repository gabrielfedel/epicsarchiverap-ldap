#!/bin/bash

## This scripts stops all four Tomcats
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

function stopAppliance {
	
	export CATALINA_BASE=${DEPLOY_DIR}/$1 
	${CATALINA_HOME}/bin/catalina.sh stop
}

	function stopAll {
	echo "(i) Checking environment variables..."
	
	echo -n "  - Checking CATALINA_HOME..."
	isEnvironmentVariableSet "${CATALINA_HOME}" result
	
	if [ "$result" = true ]; then
		printf "${GREEN}Ok!${NC}\n"
	else
		printf "${RED}Failed!${NC}\n"
		echo "   - Set CATALINA_HOME to resume..."
	fi
	
	echo -n "  - Checking DEPLOY_DIR..."
	isEnvironmentVariableSet "${DEPLOY_DIR}" result
	
	if [ "$result" = true ]; then
		printf "${GREEN}Ok!${NC}\n"
	else
		printf "${RED}Failed!${NC}\n"
		echo "   - Set DEPLOY_DIR to resume..."
	fi
	
	echo "(ii) Stopping appliances..."
	
	printf "${RED}Stopping MGMT appliance...${NC}\n"
	
	stopAppliance "mgmt"
	
	printf "${RED}Stopping ENGINE appliance...${NC}\n"
	
	stopAppliance "engine"
	
	printf "${RED}Stopping ETL appliance...${NC}\n"
	
	stopAppliance "etl"
	
	printf "${RED}Stopping RETRIEVAL appliance...${NC}\n"
	
	stopAppliance "retrieval"
}
