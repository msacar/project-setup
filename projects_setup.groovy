import org.yaml.snakeyaml.Yaml

def projectsConfig = new File('/bitnami/jenkins/projects-config/projects-config.yaml').text
def yaml = new Yaml()
def projects = yaml.load(projectsConfig)



import org.yaml.snakeyaml.Yaml
import jenkins.model.Jenkins
import java.util.zip.ZipInputStream
import hudson.FilePath

def projectsConfig = new File('/bitnami/jenkins/projects-config/projects-config.yaml').text
def yaml = new Yaml()
def projects = yaml.load(projectsConfig)

def downloadAndExtractZip(url, destDir) {
    new URL(url).openStream().withStream { is ->
        def zis = new ZipInputStream(is)
        def entry
        while (entry = zis.nextEntry) {
            if (!entry.isDirectory()) {
                new File(destDir, entry.name).parentFile?.mkdirs()
                new File(destDir, entry.name).withOutputStream { os ->
                    os << zis
                }
            }
        }
    }
}

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
                        def tempDir = new FilePath(new File("${Jenkins.instance.rootDir}/workspace/${projectName}-${jobName}-temp"))
                        tempDir.deleteRecursive()
                        tempDir.mkdirs()

                        downloadAndExtractZip(projectConfig.directUrl, tempDir.remote)

                        def jenkinsfilePath = new FilePath(tempDir, jenkinsfile)
                        script(jenkinsfilePath.readToString())
                    }
                }
            }
        }
    }
}
