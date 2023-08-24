#!/bin/bash

curl -H "Authorization: token $1" \
  -o data/source.yaml \
  -L https://raw.githubusercontent.com/simonas-dev/quies-data/main/data.yaml
