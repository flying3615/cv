#!/usr/bin/env bash
./mvnw -Pprod package -Dmaven.test.skip=true docker:build -e
cv_id=`docker images -q cv`
docker tag $cv_id flying3615/cv:latest
docker push flying3615/cv:latest
pwd=`pwd`
ansible -i $pwd/inventory all -m script -a "$pwd/remote_cv.sh"
