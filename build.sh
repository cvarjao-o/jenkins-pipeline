#!/usr/bin/env bash
set -e
export OCP_PIPELINE_CLI_URL='https://raw.githubusercontent.com/BCDevOps/ocp-cd-pipeline/822d5770b5742a60fd31a43a477ab4faf94c260a/src/main/resources/pipeline-cli'
export OCP_PIPELINE_VERSION='0.0.4'

culr -sSl "${OCP_PIPELINE_CLI_URL}" | bash -s build --config=.pipeline/config.groovy --pr=1
