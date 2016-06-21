## This plugin allows you to start up docker containers for testing.


#### Configure the plugin and test annotations
```java
plugins {
    id "io.advantageous.docker-test" version "0.1.2"
}
...

//For docker test annotations
dependencies {
    testCompile 'io.advantageous.gradle:docker-test-plugin:0.1.0'
}

//Exclude docker tests as unit tests
test {
    useJUnit {
        excludeCategories 'io.advantageous.test.DockerTest'
    }
}
```

#### Configure your test containers in your build
```java


testDockerContainers {
    postgresql {
        publishAll true
        containerName "postgresql"
        env (name:"PGSQL_ROLE_1_USERNAME", value:"docker")
        env ("PGSQL_ROLE_1_PASSWORD":"docker", "PGSQL_DB_1_NAME":"mydb")
        portMapping(container: 5432, host: 5432)
        image "tozd/postgresql:9.5"
        waitAfterRun 20
    }

    
    cassandra {
        publishAll true
        containerName "cassandra"
        portMapping(container: 9042, host: 9042)
        image "cassandra:2.2.5"
        waitAfterRun 20
    }
}
```

#### Start test containers for testing in your IDE

```sh 
$ gradle startTestDocker
```

#### Mark your test classes that need these containers 

```java

import io.advantageous.test.DockerTest

@Category(DockerTest::class)
public class FanCountBackFillJobTest {
    @Test
    fun doRun() {
```


#### To run tests that need docker use dockerTest

```sh
$ gradle clean dockerTest build  
```


