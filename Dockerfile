FROM maven
COPY . /usr/src/mymaven
WORKDIR /usr/src/mymaven
# 打包，删除原文件
RUN mvn package && \
	cd target && \
	ls
ENTRYPOINT ["/bin/bash", "ls"]
FROM primetoninc/jdk
ARG jarName=client1-0.0.1-SNAPSHOT
MAINTAINER ltl
#修改java home值，之前值错误，无-。
ENV JAVA_HOME="/usr/local/jdk-${JAVA_VERSION}" \
    PATH="${PATH}:/usr/local/jdk-${JAVA_VERSION}/bin"
#复制文件，设置工作目录
COPY --from=0 /usr/src/mymaven/target/${jarName}.jar /app/${jarName}.jar
RUN cd /app && ls
WORKDIR /app
#测试
RUN  /bin/echo 'root:123456' |chpasswd \
     && useradd ltl \
     && /bin/echo 'ltl:lkk' |chpasswd \
     && chmod -R a+w /app
EXPOSE 8080 8081 9095
#启动springbootTest；使用绝对路径指定配置文件、日志位置；WORKDIR指定工作目录
ENTRYPOINT ["java","-jar","/app/client1-0.0.1-SNAPSHOT.jar","1>/app/log/log.log","2>/app/log/err.log","&"]

