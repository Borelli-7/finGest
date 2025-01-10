#!/bin/sh

mvn clean verify sonar:sonar \
  -Dsonar.projectKey=finGest \
  -Dsonar.projectName='finGest' \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=sqp_0c0e07496d7f4c9aee93c0d95cec6fa619bf5cb6