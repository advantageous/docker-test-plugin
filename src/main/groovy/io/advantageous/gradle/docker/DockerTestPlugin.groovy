package io.advantageous.gradle.docker

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test

public class DockerTestPlugin implements Plugin<Project> {
    void apply(Project project) {

        def testDockerContainers = project.container(DockerContainer)
        project.extensions.testDockerContainers = testDockerContainers

        project.task("showDockerContainers") << {
            project.extensions.testDockerContainers.forEach {
                println it
            }
        }

        project.task("initDocker", description: "Initialize the docker environment. (docker-machine on mac)") << {
            DockerUtils.initDocker()
        }
        def initDocker = project.tasks.getByName("initDocker")

        project.task("startTestDocker", dependsOn: initDocker,
                description: "Start up dependent docker containers for testing") << {
            project.extensions.testDockerContainers.forEach {
                def result = DockerUtils.runCommand it.runCommand()
                if (result[0] != 0) throw new IllegalStateException(result[1].toString())

                if (it.waitAfterRun > 0) {
                    sleep(it.waitAfterRun * 1_000)
                }
            }
        }
        def startTestDocker = project.tasks.getByName("startTestDocker")

        project.task("stopTestDocker", description: "Stop docker containers used in tests") << {
            project.extensions.testDockerContainers.forEach {
                def result = DockerUtils.stopContainer it.getContainerName()
                if (result[0] != 0) throw new IllegalStateException(result[1].toString())
                result = DockerUtils.removeContainer it.getContainerName()
                if (result[0] != 0) throw new IllegalStateException(result[1].toString())
            }
        }
        def stopTestDocker = project.tasks.getByName("stopTestDocker")

        project.task("dockerTest", type: Test, description: "Run docker integration tests") << {
            useJUnit {
                includeCategories 'io.advantageous.test.DockerTest'
            }
        }
        def dockerTest = project.tasks.getByName("dockerTest")

        dockerTest.dependsOn startTestDocker
        dockerTest.finalizedBy stopTestDocker

        project.test {
            useJUnit {
                excludeCategories 'io.advantageous.test.DockerTest'
            }
        }

    }
}
