# Spring-Dynamic-Properties
Spring 动态刷新配置。

## 快速开始

mvn package

1.启动

java -jar apollo-spring-customer-0.0.1-SNAPSHOT.jar

2.读取现有属性

GET http://127.0.0.1:8081/get

3.修改属性

GET http://127.0.0.1:8081/updateName?newName=xxx

4.查看属性修改的效果

GET http://127.0.0.1:8081/get
