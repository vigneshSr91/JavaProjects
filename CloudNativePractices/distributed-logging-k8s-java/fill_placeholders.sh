#!/bin/sh

if [ "$#" -ne 3 ]
then
  echo "Incorrect Usage: Not all argument were provided."
  echo "Example Usage:"
  echo "./fill_placeholders.sh i5300000 my-cluster my-project"
  exit 1
fi

cd "$(dirname "$0")" || exit 1

sed -i.bak "s/<YOUR C\/D\/I-NUMBER>/$1/g" deployment/apps/2_users.yaml || exit 1
sed -i.bak "s/<YOUR C\/D\/I-NUMBER>/$1/g" deployment/apps/3_greetings.yaml || exit 1
sed -i.bak "s/<CLUSTER>.<PROJECT>/$2.$3/g" deployment/apps/3_greetings.yaml || exit 1
sed -i.bak "s/<CLUSTER>.<PROJECT>/$2.$3/g" deployment/efk-stack/3_kibana.yaml || exit 1
sed -i.bak "s/<CLUSTER>.<PROJECT>/$2.$3/g" create_index_pattern.sh || exit 1

echo "All placeholders were replaced successfully."