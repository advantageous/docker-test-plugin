package io.advantageous.test;

import io.advantageous.boon.Runner;

import java.net.URI;
import java.util.Arrays;

import static java.lang.System.getenv;

public class DockerHostUtils {

    private static final boolean IS_MAC = System.getProperty("os.name").toLowerCase().contains("mac");

    private DockerHostUtils() {
    }

    public static String getDockerHost() {
        final String dockerHost = getenv("DOCKER_HOST");

        if (IS_MAC && !(dockerHost == null)) {

            final String virtualBoxString = Arrays.asList(Runner.run("docker info").split("\n"))
                    .stream().filter(line -> line.contains("provider=virtualbox"))
                    .findAny()
                    .orElse(null);
            if (virtualBoxString == null) {
                return "127.0.0.1";
            } else {
                return "192.168.99.100";
            }
        } else {
            return dockerHost != null ? URI.create(dockerHost).getHost() : "127.0.0.1";
        }
    }

    public static String getDockerHostAndPort() {
        final String socatPort = getenv("SOCAT_PORT") != null ? getenv("SOCAT_PORT") : "2375";
        return getDockerHost() + ':' + socatPort;
    }
}
