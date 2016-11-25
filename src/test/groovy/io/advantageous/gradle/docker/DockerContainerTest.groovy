package io.advantageous.gradle.docker

import org.apache.tools.ant.taskdefs.condition.Os
import org.junit.Test
import org.testng.Assert

class DockerContainerTest {

    @Test
    void testRunCommand_nonWin() throws Exception {

        def socat = new DockerContainer("socat")
                .containerName("docker-http")
                .portMapping(container: 2375, host: 2375)
                .volume(container: "/var/run/docker.sock", host: "/var/run/docker.sock")
                .image("sequenceiq/socat").env("foo":"bar")
                .runCommand()

        def expected = Os.isFamily(Os.FAMILY_WINDOWS) ?
                "docker run -d -p 2375:2375 --volume=/var/run/docker.sock:/var/run/docker.sock --env=FOO=bar --name=docker-http sequenceiq/socat":
                "docker run -d -p 2375:2375 --volume=/var/run/docker.sock:/var/run/docker.sock --env='FOO=bar' --name=docker-http sequenceiq/socat" ;

        Assert.assertEquals(expected, socat)
    }
}
