#!/bin/sh

HEAP_SIZE="${HEAP_SIZE:-400m}"
APP_NAME="${APP_NAME:-ratelimiter}"
APP_PORT="${APP_PORT:-8097}"
LOG_JVM_OPTS="${LOG_JVM_OPTS:-true}"

JAVA_OPTS="$JAVA_OPTIONS -Xms${HEAP_SIZE} -Xmx${HEAP_SIZE} -Djdk.module.illegalAccess=deny -Dspring.application.name=${APP_NAME} -Dspring.main.banner-mode=off -DPORT=${APP_PORT}"

eval "java $JAVA_OPTS  -jar ratelimiter.jar"
