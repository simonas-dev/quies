#!/bin/bash

openssl enc -aes-256-cbc \
    -K "7b877b6e14f7bffd15f9dc04ad187be016bcc6e96e298b6607579e3cb56a1743" \
    -iv "3d769525eb6fb963c7a507fa26ef8df5" \
    -in data/source.yaml \
    -out data/src/main/assets/data.yaml.enc