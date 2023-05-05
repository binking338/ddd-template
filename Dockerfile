# syntax = docker/dockerfile:1.3
FROM maven:3-jdk-8 AS build
WORKDIR /usr/src/app
COPY . .
RUN if [ -e **/src/main/**/logback-prod.xml ]; then mv -f $(ls **/src/main/**/logback-prod.xml) $(ls **/src/main/**/logback.xml); fi
RUN java -version &\
    mvn -version
RUN --mount=type=cache,target=/.m2/repository \
    mvn -Dmaven.repo.local=/.m2/repository clean -U package -s ./.m2/settings.xml -Dfile.encoding=UTF-8 -DskipTests=true

FROM registry.cn-hangzhou.aliyuncs.com/hunliji/centos7-openjdk8-jdk:2.2.0
USER root
ENV LANG en_US.UTF-8
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

COPY --from=build /usr/src/app/target/*.jar /app.jar

ENV JAVA_APP_JAR=/app.jar
ENV JAVA_APP_DIR=/

ENV JAVA_OPTIONS="-server -Dserver.port=80 -Djava.security.egd=file:/dev/./urandom -Drocketmq.client.logUseSlf4j=true \
    -XX:InitialRAMPercentage=70.0 -XX:MaxRAMPercentage=70.0 -XX:MinRAMPercentage=70.0 -XX:NewRatio=2 -Xss256k -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m -XX:MaxDirectMemorySize=256m -XX:-OmitStackTraceInFastThrow -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+ParallelRefProcEnabled -XX:+CMSScavengeBeforeRemark -XX:+UseCMSCompactAtFullCollection -XX:CMSInitiatingOccupancyFraction=65 -XX:+UseCMSInitiatingOccupancyOnly -XX:+ExplicitGCInvokesConcurrentAndUnloadsClasses \
    -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/heap/ddd-template.hprof -Xloggc:/gc.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps"
VOLUME /heap
ENTRYPOINT ["sh", "/deployments/run-java.sh"]
