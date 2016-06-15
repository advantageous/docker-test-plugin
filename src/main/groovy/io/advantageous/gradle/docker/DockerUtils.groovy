package io.advantageous.gradle.docker

class DockerUtils {

    static void initDocker() {

        def runningResult = runCommand "docker", "inspect", "-f", "{{.State.Running}}", "docker-http"
        println "Docker running check: " + runningResult[1]
        if (runningResult[1] != "true") {
            println "Starting socat"
            def dockerId = runCommand("docker ps -q -l".split(" "))[1]
            String[] command
            if (dockerId.toString().length() > 1) {
                command = ["docker", "start", dockerId]
            } else {
                command = new DockerContainer("socat")
                        .containerName("docker-http")
                        .portMapping(container: 2375, host: 2375)
                        .image("sequenceiq/socat")
                        .volume(container: "/var/run/docker.sock", host: "/var/run/docker.sock")
                        .runCommand()
            }

            def result = runCommand command
            if (result[0] != 0) {
                throw new IllegalStateException(result[1].toString())
            }
        }
    }

    static Map<String, String> getDockerEnv() {

        def envMap = [:]
        if ("Mac OS X".equals(System.getProperty("os.name"))) {
            if ("which docker-machine".execute().text) {
                if ("docker info".execute().text.contains("Cannot connect to the Docker daemon")) {
                    println "Exporting docker ENV"
                    envMap = "docker-machine env default".execute().text
                            .split('\n')
                            .findAll { it.startsWith('export') }
                            .collect { it.replace('export', '').replace('"', '').trim() }
                            .collect { it.split('=') }
                            .collectEntries { [it[0], it[1]] }
                }
            }
        }
        envMap
    }

    private static GString dockerCoordinates(String namespace, String projectName, String projectVersion) {
        "$namespace/${projectName}:${projectVersion}"
    }

    static pushDocker(String namespace, String projectName, String projectVersion) {
        println("docker push ${dockerCoordinates(namespace, projectName, projectVersion)}")
        runCommand("docker", "push", dockerCoordinates(namespace, projectName, projectVersion))
    }

    static runDocker(String namespace, String projectName, String projectVersion) {
        println('docker run -P ' + dockerCoordinates(namespace, projectName, projectVersion))
        runCommand("docker", "run", "-P", dockerCoordinates(namespace, projectName, projectVersion))
    }

    static buildDocker(String namespace, String projectName, String projectVersion) {
        println("docker build -t ${dockerCoordinates(namespace, projectName, projectVersion)} build/")
        runCommand("docker", "build", "-t", dockerCoordinates(namespace, projectName, projectVersion), "build/")
    }

    static runCommand(String... command) {

        def stringBuilder = new StringBuilder()
        def processBuilder = new ProcessBuilder()
                .command(command)
                .redirectErrorStream(true)

        processBuilder.environment().putAll(getDockerEnv())

        def process = processBuilder.start()
        process.waitFor()

        process.inputStream.eachLine {
            println it
            stringBuilder.append(it)
        }

        println(stringBuilder.toString())

        [process.exitValue(), stringBuilder.toString()]
    }

    static stopContainer(String containerName) {
        println("Stop " + containerName)
        runCommand "docker", "stop", containerName
    }

    static removeContainer(String containerName) {
        println("Remove " + containerName)
        runCommand "docker", "rm", containerName
    }
}
