import org.yaml.snakeyaml.Yaml
import jenkins.model.Jenkins
import java.util.zip.ZipInputStream
import hudson.FilePath

def projectsConfig = new File('/bitnami/jenkins/projects-config/projects-config.yaml').text
def yaml = new Yaml()
def projects = yaml.load(projectsConfig)

def additionalProjectsConfig = new File('/bitnami/jenkins/home/workspace/project-config/project-config.yaml').text
def additionalYaml = new Yaml()
def additionalProjects = additionalYaml.load(additionalProjectsConfig)

def mergedProjects = projects.projects + additionalProjects.projects

mergedProjects.each { projectName, projectConfig ->
    folder(projectName) {
        description("Folder for ${projectName}")
    }

    projectConfig.jobs.each { jobName, jenkinsfile ->
        pipelineJob("${projectName}/${jobName}") {
            definition {
                if (projectConfig.gitUrl) {
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
                } else if (projectConfig.directUrl) {
                    cps {
                        // NOT IMPLEMENTED
                    }
                }
                else {
                    //default jenkinsfile

                }

                // we can set environmentVariables to the pipelines from initial-projects.yaml
//                 environmentVariables {
//                     env('PROJECT_ALIAS', projectConfig.PROJECT_ALIAS)
//                 }
            }
        }
    }
}
