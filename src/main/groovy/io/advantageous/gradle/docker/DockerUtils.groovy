package io.advantageous.gradle.docker

class DockerUtils {

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

    static runCommand(String command) {


        println("Running command $command")

        String[] args = ["/bin/sh", "-c",  "$command"]

        def stringBuilder = new StringBuilder()
        def processBuilder = new ProcessBuilder()
                .command(args)
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
        runCommand "docker stop $containerName"
    }

    static removeContainer(String containerName) {
        println("Remove " + containerName)
        runCommand "docker rm $containerName"
    }
}
