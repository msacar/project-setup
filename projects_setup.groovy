import org.yaml.snakeyaml.Yaml

def projectsConfig = new File('/bitnami/jenkins/projects-config/projects-config.yaml').text
def yaml = new Yaml()
def projects = yaml.load(projectsConfig)


projects.projects.each { projectName, projectConfig ->
    folder(projectName) {
        description("Folder for ${projectName}")
    }

    projectConfig.jobs.each { jobName, jenkinsfile ->
        pipelineJob("${projectName}/${jobName}") {
            definition {
                cpsScm {
                    scm {
                        git {
                            remote {
                                url(projectConfig.gitUrl)
                            }
                            branch(projectConfig.branch)
                        }
                    }
                    scriptPath(jenkinsfile)
                }
            }
        }
    }
}
