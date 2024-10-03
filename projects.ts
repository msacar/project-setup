const yaml = require('js-yaml')
const MongoClient = require('mongodb').MongoClient;
const { MONGO_CONNECTION_URI } = process.env;

console.log(MONGO_CONNECTION_URI)
const client = new MongoClient(MONGO_CONNECTION_URI);

client.connect().then(() => console.log('Connected!')).catch(console.error);
async function main() {
    const collection = client.db("root").collection("Projects");
    const projects = await collection.find()
    console.log(projects)
}

main()