#!/bin/bash

DEPLOY_DIR=$1
WARSRC_DIR=$2

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

function extractWar {
	
	rm -rf ${DEPLOY_DIR}/$1/webapps
        mkdir ${DEPLOY_DIR}/$1/webapps
	cp ${WARSRC_DIR}/$1.war ${DEPLOY_DIR}/$1/webapps/
	mkdir ${DEPLOY_DIR}/$1/webapps/$1
	(cd ${DEPLOY_DIR}/$1/webapps/$1; jar xf ../$1.war;)
	rm ${DEPLOY_DIR}/$1/webapps/$1.war
	
}

echo "(i) Checking environment variables..."

echo -n "  - Checking WARSRC_DIR..."
isEnvironmentVariableSet "${WARSRC_DIR}" result

if [ "$result" = true ]; then
	printf "${GREEN}Ok!${NC}\n"
else
	printf "${RED}Failed!${NC}\n"
	echo "   - Set WARSRC_DIR to resume..."
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

extractWar mgmt
extractWar engine
extractWar etl
extractWar retrieval

#$ pushd ${DEPLOY_DIR}/mgmt/webapps && rm -rf mgmt*; cp ${WARSRC_DIR}/mgmt.war .; mkdir mgmt; cd mgmt; jar xf ../mgmt.war; popd; 
#$ pushd ${DEPLOY_DIR}/engine/webapps && rm -rf engine*; cp ${WARSRC_DIR}/engine.war .; mkdir engine; cd engine; jar xf ../engine.war; popd; 
#$ pushd ${DEPLOY_DIR}/etl/webapps && rm -rf etl*; cp ${WARSRC_DIR}/etl.war .; mkdir etl; cd etl; jar xf ../etl.war; popd; 
#$ pushd ${DEPLOY_DIR}/retrieval/webapps && rm -rf retrieval*; cp ${WARSRC_DIR}/retrieval.war .; mkdir retrieval; cd retrieval; jar xf ../retrieval.war; popd;
