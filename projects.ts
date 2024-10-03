const yaml = require('js-yaml')
const MongoClient = require('mongodb').MongoClient;
const { MONGO_CONNECTION_STRING } = process.env;

console.log(MONGO_CONNECTION_STRING)
const client = new MongoClient(MONGO_CONNECTION_STRING);

client.connect().then(() => console.log('Connected!')).catch(console.error);
async function main() {
    const collection = client.db("root").collection("Projects");
    const projects = await collection.find().toArray()
    console.log(projects)
}

main()