const fs = require('fs')
const yaml = require('js-yaml')
const MongoClient = require('mongodb').MongoClient;
const { MONGO_CONNECTION_STRING } = process.env;

console.log(MONGO_CONNECTION_STRING)
const client = new MongoClient(MONGO_CONNECTION_STRING);

client.connect().then(() => console.log('Connected!')).catch(console.error);

async function main() {
    const collection = client.db("root").collection("Project");
    const projectsData = await collection.find().toArray()
    const projects =  projectsData.map(p => {
        return {
            [p._rio_pk]: {
                gitUrl: "git@github.com:rettersoft/rio-kubernetes-jenkinsfiles.git",
                branch: "main",
                jobs: {
                    build: "user-code-template/build.Jenkinsfile",
                    deploy: "user-code-template/deploy.Jenkinsfile"
                }
            }
        }
    })
    fs.writeFileSync("project-config.yaml",yaml.dump({projects}))
    console.log({yaml : yaml.dump({projects})})
    console.log(projects)
    process.exit(0)
}

main().catch(err=>{
    console.error(err)
    process.exit(1)
});