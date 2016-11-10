./mvnw -Pprod package -Dmaven.test.skip=true docker:build -e
cv_id=`docker images -q cv`
docker tag $cv_id flying3615/cv:latest
docker push flying3615/cv:latest
