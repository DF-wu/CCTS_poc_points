#!/bin/bash
docker run --rm  pactfoundation/pact-cli:latest broker can-i-deploy --pacticipant 'Example App' --broker-base-url 23.dfder.tw:10141 --latest
docker run --rm -w ${PWD} -v ${PWD}:${PWD} pactfoundation/pact-cli:latest publish ${PWD}/Contracts/pacts  --broker-base-url 23.dfder.tw:10141  --consumer-app-version "v0.1" --tag-with-git-branch
