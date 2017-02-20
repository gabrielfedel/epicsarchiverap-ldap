#!/bin/bash

source ${DEPLOY_DIR}/scripts/start-appliances.sh
source ${DEPLOY_DIR}/scripts/stop-appliances.sh

case "$1" in
   start)
		echo "entrou"
		startAll
   ;;
   stop)
		stopAll
   ;;
   restart)
	    	stopAll
    		startAll
   ;;
   *)
      echo "Usage: $0 {start|stop|restart}"
esac
