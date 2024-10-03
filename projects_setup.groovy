import org.yaml.snakeyaml.Yaml
import jenkins.model.Jenkins
import java.util.zip.ZipInputStream
import hudson.FilePath

def projectsConfig = new File('/bitnami/jenkins/projects-config/projects-config.yaml').text
def yaml = new Yaml()
def projects = yaml.load(projectsConfig)

/**
*
* i need to connect mongodb in here
* i think i can do it with running js script here
* then i need to get results js script
* then i need to convert result to yaml
* then i need to merge with projects variable defined in line:8 def projects = yaml.load(projectsConfig)
**/
// Trigger the data retrieval job with parameters
def dataRetrievalBuild = build job: 'project-config', wait: true, parameters: []


projects.projects.each { projectName, projectConfig ->
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
