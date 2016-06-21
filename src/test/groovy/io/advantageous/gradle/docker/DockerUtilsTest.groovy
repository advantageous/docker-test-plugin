package io.advantageous.gradle.docker

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
        Assert.assertEquals 0, result[0]

        result = DockerUtils.stopContainer "httpd"
        Assert.assertEquals 0, result[0]

        result = DockerUtils.removeContainer "httpd"
        Assert.assertEquals 0, result[0]
    }
}
