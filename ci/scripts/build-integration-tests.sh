#!/bin/bash
set -e

source $(dirname $0)/common.sh
repository=$(pwd)/distribution-repository

pushd git-repo > /dev/null
run_maven -f spring-boot-tests/spring-boot-integration-tests/pom.xml clean install -Dfull -Drepository=file://${repository}
popd > /dev/null
