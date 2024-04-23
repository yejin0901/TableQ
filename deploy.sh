#!/usr/bin/env bash

REPOSITORY=/home/ec2-user/tabling
cd $REPOSITORY

APP_NAME=tabling
JAR_NAME=$(ls $REPOSITORY/build/libs/ | grep 'SNAPSHOT.jar' | tail -n 1)
JAR_PATH=$REPOSITORY/build/libs/$JAR_NAME

CURRENT_PID=$(pgrep -f $APP_NAME)

if [ -z $CURRENT_PID ]
then
  echo "> 종료할 애플리케이션이 없습니다."
else
  echo "> kill -9 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

nohup java -jar /home/ec2-user/tabling/build/libs/team11_tabling-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev &

