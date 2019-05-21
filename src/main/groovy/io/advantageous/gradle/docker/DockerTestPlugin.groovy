package io.advantageous.gradle.docker

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test

public class DockerTestPlugin implements Plugin<Project> {
    void apply(Project project) {

        def testDockerContainers = project.container(DockerContainer)
        project.extensions.testDockerContainers = testDockerContainers

        project.task("showDockerContainers") {
            doLast {
                project.extensions.testDockerContainers.forEach { println it }
            }
        }

        project.task("startTestDocker", description: "Start up dependent docker containers for testing") {
            doLast {
                project.extensions.testDockerContainers.forEach {
                    def result = DockerUtils.runCommand it.runCommand()
                    if (result[0] != 0) throw new IllegalStateException(result[1].toString())
                    if (it.waitAfterRun > 0) {
                        sleep(it.waitAfterRun * 1_000)
                    }
                }
            }
        }
        def startTestDocker = project.tasks.getByName("startTestDocker")

        project.task("stopTestDocker", description: "Stop docker containers used in tests") {
            doLast {
                project.extensions.testDockerContainers.forEach {
                    def result = DockerUtils.stopContainer it.getContainerName()
                    if (result[0] != 0) throw new IllegalStateException(result[1].toString())
                    result = DockerUtils.removeContainer it.getContainerName()
                    if (result[0] != 0) throw new IllegalStateException(result[1].toString())
                }
            }
        }
        def stopTestDocker = project.tasks.getByName("stopTestDocker")

        project.task("dockerTest", type: Test, description: "Run docker integration tests") {
            doLast {
                useJUnit {
                    includeCategories 'io.advantageous.test.DockerTest'
                }
            }
        }
        def dockerTest = project.tasks.getByName("dockerTest")

        dockerTest.dependsOn startTestDocker
        dockerTest.finalizedBy stopTestDocker

        //TODO: need to figure out how to make the normal tests skip these tests
//        project.test {
//            useJUnit {
//                excludeCategories 'io.advantageous.test.DockerTest'
//            }
//        }
    }
}
