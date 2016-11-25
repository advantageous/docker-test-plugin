package io.advantageous.gradle.docker

import org.junit.Test
import org.testng.Assert

class DockerUtilsTest {
    final SAMPLE = "docker run -d -p 2375:2375 --volume=/var/run/docker.sock:/var/run/docker.sock --env='FOO=bar' --name=docker-http sequenceiq/socat"

    @Test
    void prepareArgs_windows() throws Exception {
        def actualWin = DockerUtils.prepareArgs(SAMPLE, true)
        Assert.assertEquals("cmd", actualWin.get(0))
        Assert.assertEquals("/c", actualWin.get(1))
        Assert.assertEquals(SAMPLE, actualWin.get(2))
    }

    @Test
    void prepareArgs_linux() throws Exception {
        def actualLinux = DockerUtils.prepareArgs(SAMPLE, false)
        Assert.assertEquals("/bin/sh", actualLinux.get(0))
        Assert.assertEquals("-c", actualLinux.get(1))
        Assert.assertEquals(SAMPLE, actualLinux.get(2))
    }
}
