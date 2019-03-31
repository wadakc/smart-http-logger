# smart-http-logger
auto http-logger for spring-boot

![apache licensed](https://img.shields.io/badge/License-Apache_2.0-d94c32.svg)
![Java](https://img.shields.io/badge/Language-Java-f88909.svg)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/jp.spring-boot-reference/smart-http-logger/badge.svg)](https://maven-badges.herokuapp.com/maven-central/jp.spring-boot-reference/smart-http-logger)

https://search.maven.org/artifact/jp.spring-boot-reference/smart-http-logger/1.0.0-SNAPSHOT/jar
https://oss.sonatype.org/content/repositories/releases/jp/spring-boot-reference/smart-http-logger/

`smart-http-logger` is  http logging library for `SpringBoot` Project.





## introduction

```javaScript

019-03-31 17:05:47.811  INFO 8706 --- [nio-8888-exec-2] j.s.smarthttplogger.LoggingPrinter       : 
{
 "url":"GET:/user/6",                              // method and request url path.
 "requestHeaders":{
   "host":"xxxxxxxxxxxxxxxx","user-agent":"curl/7.54.0",
   "accept":"application/json"
 },                                                // requestHeaders/
 "requestBody":null,                               // request body (when method equals GET, this will be null.)
 "responseHeaders":{},                             // response headers
 "httpStatus":200,                                 // httpstatus
 "responseBody":{"id":"6","name":"kc","score":100} // responsebody
}

```

## Usage

- 1. adding dependency on pom

```xml
<dependency>
	<groupId>jp.spring-boot-reference</groupId>
	<artifactId>smart-http-logger</artifactId>
	<version>1.0.0</version>
</dependency>

```

- 2. scan this package to enable automatically logging.

```java
@SpringBootApplication
@ComponentScan(basePackages = {"com.example.api","jp.springbootreference.smarthttplogger"}) // here.
public class ApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}

}
```

-3. to hide privacy , you can set what header's information you hide by adding setting on `application.properties` like below.

```yaml
smartlog.header.secrets=Authorization,host
```

then, `Authorization` and `host` header's information is hidden by `xxxxxxxxxxxxxx` expression.


## installation

adding dependency on pom.xml

```xml
<dependency>
	<groupId>jp.spring-boot-reference</groupId>
	<artifactId>smart-http-logger</artifactId>
	<version>1.0.0</version>
</dependency>
```

