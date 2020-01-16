FROM maven
COPY . /usr/src/mymaven
WORKDIR /usr/src/mymaven
# 打包
RUN mvn clean package -Dmaven.test.skip=true && \
	cd target && \
	ls
ENTRYPOINT ["/bin/bash", "ls"]
FROM primetoninc/jdk
# jar包名，不包含.jar后缀
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
#使用的端口，必须在此打开
EXPOSE 6713 8080 8081 9095
#启动springbootTest；使用绝对路径指定配置文件、日志位置；WORKDIR指定工作目录
ENTRYPOINT java -jar /app/${jarName}.jar 1>/app/log/log.log 2>/app/log/err.log

