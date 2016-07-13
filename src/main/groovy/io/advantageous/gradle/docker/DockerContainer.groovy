package io.advantageous.gradle.docker

class DockerContainer {

    private final String name

    private String containerName
    private String image
    private String hostName
    private boolean publishAll
    private ports = [:]
    private volumes = [:]
    private env = [:]
    private boolean detach = true
    private String runArgs

    private int waitAfterRun = 0

    void waitAfterRun(int waitAfterRun) {
        this.waitAfterRun = waitAfterRun
    }

    DockerContainer(String name) {
        this.name = name
    }

    DockerContainer containerName(String containerName) {
        this.containerName = containerName
        return this
    }

    DockerContainer image(String image) {
        this.image = image
        return this
    }

    DockerContainer runArgs(String runArgs) {
        this.runArgs = runArgs
        return this
    }

    DockerContainer hostName(String hostName) {
        this.hostName = hostName
        return this
    }

    DockerContainer publishAll(boolean publishAll) {
        this.publishAll = publishAll
        return this
    }

    public String toString() {
        name
    }

    DockerContainer portMapping(Map map) {
        ports[map.host] = map.container
        return this
    }


    DockerContainer env(Map map) {

        if (map.containsKey("name") && map.containsKey("value") && map.size() ==2) {
            env[map.name] = map.value
        } else {
            env.putAll(map)
        }
        return this
    }

    DockerContainer detach(boolean detach) {
        this.detach = detach
        return this
    }

    DockerContainer volume(Map map) {
        volumes[map.container] = map.host
        return this
    }

    String getContainerName() {
        return containerName
    }

    public String runCommand() {
        StringBuilder builder = new StringBuilder()

        builder.append("docker run")

        if (detach) builder.append(" -d")

        if (ports.size() > 0) {
            ports.entrySet().stream().forEach { entry ->
                builder.append(" -p ").append(entry.key).append(':').append(entry.value)
            }
        }

        if (publishAll) builder.append(" -P")

        if (volumes.size() > 0) {
            volumes.entrySet().stream().forEach { entry ->
                builder.append(" --volume=").append(entry.key).append(':').append(entry.value)
            }
        }

        if (env.size() > 0) {
            env.entrySet().stream().forEach { entry ->
                builder.append(" --env=")
                        .append("'")
                        .append(entry.key.toString().toUpperCase())
                        .append('=')
                        .append(entry.value)
                        .append("'")

            }
        }


        if (containerName) {
            builder.append(" --name=").append(containerName)
        }


        if (hostName) {
            builder.append(" --hostname=").append(hostName)
        }

        if (image) {
            builder.append(" ").append(image)
        }

        if (runArgs) {
            builder.append(" ").append(runArgs)
        }

        builder.toString()
    }

}
