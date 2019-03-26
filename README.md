# Spring Cloud Connectors ICD

Extensions to Spring Cloud Connectors to understand IBM Cloud Databases VCAP_SERVICES information.

## Using the library in your Java projects

We use jitpack to build this library, which means you can direct maven or gradle directly to our github releases to satisfy dependencies.

1. include jitpack.io in your list of repositories:
  * In maven:
  ```
    <repositories>
      <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
      </repository>
    </repositories>
  ```
  * In gradle:
  ```
    repositories {
      maven { url "https://jitpack.io" }
    }
  ```
2. Include version of the library in your dependencies:
  * In maven:
  ```
    <dependency>
      <groupId>com.github.bardweller</groupId>
      <artifactId>spring-cloud-connectors-icd</artifactId>
      <version>v0.2</version>
    </dependency>
  ```
  * In gradle:
  ```
    dependencies {
	    compile 'com.github.bardweller:spring-cloud-connectors-icd:v0.2'
    }
  ```

3. Use with..

- Databases for PostgreSQL

(other ICD coming soon)