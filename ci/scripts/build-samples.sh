#!/bin/bash
set -e

source $(dirname $0)/common.sh
repository=$(pwd)/distribution-repository

pushd git-repo > /dev/null
run_maven -f spring-boot-samples/pom.xml clean install -U -Dfull -Drepository=file://${repository}
popd > /dev/null
