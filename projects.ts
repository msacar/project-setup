import * as fs from 'fs';
import * as yaml  from 'js-yaml';
import { MongoClient, Document } from 'mongodb';
const { MONGO_CONNECTION_STRING } = process.env;

if (!MONGO_CONNECTION_STRING) {
    throw new Error('MONGO_CONNECTION_STRING environment variable is not set');
}

console.log(MONGO_CONNECTION_STRING)
const client = new MongoClient(MONGO_CONNECTION_STRING);

client.connect().then(() => console.log('Connected!')).catch(console.error);

interface Project extends Document{
    _rio_pk: string;
}

async function main() {
    const collection = client.db("root").collection<Project>("Project");
    const projectsData = await collection.find().toArray()
    const projects = projectsData.filter(p => p._rio_pk != 'root').map(p => {
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
    fs.writeFileSync("project-config.yaml", yaml.dump({projects}))
    console.log({yaml: yaml.dump({projects})})
    console.log(projects)
    process.exit(0)
}

main().catch(err => {
    console.error(err)
    process.exit(1)
});
