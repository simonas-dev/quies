#!/bin/bash

# Example usage
# ./data/replaceSecrets.sh \
# -k 7b877b6e14f7bffd15f9dc04ad187be016bcc6e96e298b6607579e3cb56a1743 \
# -i 3d769525eb6fb963c7a507fa26ef8df5

while getopts k:i: flag
do
    case "${flag}" in
        k) key=${OPTARG};;
        i) iv=${OPTARG};;
        *) echo "Wrong flag. Read the source code."
    esac
done

# Replace example keys in asset encryption script
sed "s/7b877b6e14f7bffd15f9dc04ad187be016bcc6e96e298b6607579e3cb56a1743/$key/" \
data/createAsset.sh \
> data/createAsset.sh.tmp
mv data/createAsset.sh.tmp data/createAsset.sh

sed "s/3d769525eb6fb963c7a507fa26ef8df5/$iv/" \
data/createAsset.sh \
> data/createAsset.sh.tmp
mv data/createAsset.sh.tmp data/createAsset.sh

# Replace example keys in runtime secrets
sed "s/7b877b6e14f7bffd15f9dc04ad187be016bcc6e96e298b6607579e3cb56a1743/$key/" \
data/src/main/cpp/q_secrets.cpp \
> data/src/main/cpp/q_secrets.cpp.tmp
mv data/src/main/cpp/q_secrets.cpp.tmp data/src/main/cpp/q_secrets.cpp

sed "s/3d769525eb6fb963c7a507fa26ef8df5/$iv/" \
data/src/main/cpp/q_secrets.cpp \
> data/src/main/cpp/q_secrets.cpp.tmp
mv data/src/main/cpp/q_secrets.cpp.tmp data/src/main/cpp/q_secrets.cpp