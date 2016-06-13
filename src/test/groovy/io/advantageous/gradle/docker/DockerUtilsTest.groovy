package io.advantageous.gradle.docker

import io.advantageous.gradle.docker.DockerContainer
import io.advantageous.gradle.docker.DockerUtils
import org.junit.Assert
import org.junit.Test

class DockerUtilsTest {

    @Test
    void testInitDocker() throws Exception {
        DockerUtils.initDocker()
        Assert.assertNotNull(DockerUtils.dockerEnv)
    }

    @Test
    void testRunSocat() throws Exception {

        def command = new DockerContainer("httpd")
                .containerName("httpd")
                .publishAll(true)
                .image("httpd")
                .runCommand()

        def result = DockerUtils.runCommand command
        println result[1]
        Assert.assertEquals 0, result[0]

        result = DockerUtils.stopContainer "httpd"
        println result[1]
        Assert.assertEquals 0, result[0]

        result = DockerUtils.removeContainer "httpd"
        println result[1]
        Assert.assertEquals 0, result[0]
    }
}
