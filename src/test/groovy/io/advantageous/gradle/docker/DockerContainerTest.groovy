package io.advantageous.gradle.docker

import org.junit.Test
import org.testng.Assert

class DockerContainerTest {

    @Test
    void testRunCommand() throws Exception {

        def socat = new DockerContainer("socat")
                .containerName("docker-http")
                .portMapping(container: 2375, host: 2375)
                .volume(container: "/var/run/docker.sock", host: "/var/run/docker.sock")
                .image("sequenceiq/socat")
                .runCommand()

        Assert.assertEquals("docker run -d -p 2375:2375 --volume=/var/run/docker.sock:/var/run/docker.sock --name=docker-http sequenceiq/socat".split(" "), socat)
    }
}
