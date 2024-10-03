const yaml = require('js-yaml')
const MongoClient = require('mongodb').MongoClient;
const uri = process.env.MONGO_CONNECTION_URI;
const client = new MongoClient(uri);
client.connect().then(() => console.log('Connected!')).catch(console.error);

async function main() {

    const collection = client.db("root").collection("Projects");
    const projects = await collection.find()


}

main()