#!/bin/sh

# usage: buildandpush.sh ENVIRONMENT(default dev) IFREBUILD (default true)
# sample: ./buildandpush.sh | ./buildandpush.sh dev | ./buildandpush.sh prod false

currentPath="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $currentPath

pushenv="1.0";
if [[ -z "$1" ]]; then
  echo "No docker Tag supplied"
else
  pushenv=$1;
fi

ifRebuild="true";
if [[ $2 == "false" ]]; then
	ifRebuild="false";
fi

git_Id=$(git rev-parse --short HEAD);
echo "Docker Tag version: "$pushenv" ; If rebuild: "$ifRebuild" ; Git id: "$git_Id;

if [[ $ifRebuild == "true" ]]; then
	sed -i "" -e 's/git-build.ver=0000000/git-build.ver='$git_Id'/g' src/main/resources/settings/settings.properties
	mvn clean package -Dmaven.test.skip=true;
	git checkout -- src/main/resources/settings/settings.properties
	docker buildx build --platform linux/amd64 -f Dockerfile-cdns -t cdns:$git_Id .
fi

docker tag cdns:$git_Id d3c86kwblj2hh2.cloudfront.net/nowtv/cdns:$pushenv
docker push d3c86kwblj2hh2.cloudfront.net/nowtv/cdns:$pushenv
