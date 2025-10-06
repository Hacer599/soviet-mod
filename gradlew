#!/bin/sh

APP_NAME="Gradle"
APP_BASE_NAME=${0##*/}

GRADLE_USER_HOME=${GRADLE_USER_HOME:-${HOME}/.gradle}

warn() {
    echo "$*"
} >&2

die() {
    echo
    echo "$*"
    echo
    exit 1
} >&2

case "$(uname)" in
CYGWIN* | MINGW*)
    APP_HOME=$(cygpath --path --mixed "${APP_HOME}")
    GRADLE_USER_HOME=$(cygpath --path --mixed "${GRADLE_USER_HOME}")
    ;;
esac

GRADLE_OPTS="${GRADLE_OPTS} -Xmx64m -Xms64m"

exec "${JAVA_HOME}/bin/java" ${GRADLE_OPTS} ${JAVA_OPTS} -classpath "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain "$@"
