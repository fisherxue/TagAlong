const supertest = require('supertest')
const app = require("../index.js");
const mongoose = require('mongoose');
const request = supertest(app);
const User = require("../User/models/user");



const databaseName = 'userroutestest'

beforeAll(async () => {
	const url = `mongodb://127.0.0.1/${databaseName}`;
  	await mongoose.connect(url, { useNewUrlParser: true })
})

async function removeAllCollections () {
	const collections = Object.keys(mongoose.connection.collections);
	for (const collectionName of collections) {
    	const collection = mongoose.connection.collections[collectionName];
    	await collection.deleteMany();
  	}
}

afterEach(async () => {
	await removeAllCollections();
})

async function dropAllCollections() {
    const collections = Object.keys(mongoose.connection.collections)
    for (const collectionName of collections) {
        const collection = mongoose.connection.collections[collectionName]
        try {
            await collection.drop();
        } catch (error) {
            // This error happens when you try to drop a collection that's already dropped. Happens infrequently. 
            // Safe to ignore. 
            if (error.message === 'ns not found') return

            // This error happens when you use it.todo.
            // Safe to ignore. 
            if (error.message.includes('a background operation is currently running')) return

            console.log(error.message);
        }
    }
}

// Disconnect Mongoose
afterAll(async () => {
    await dropAllCollections();
    await mongoose.connection.close();
})


describe('testing registration', () => {

	const demouser = {
		username: "demo",
		email: "demo@demo.com",
		password: "demodemodemo"
	}

	it('should succeeds with appropriate credentials', async (done) => {
		const res = await request.post("/users/register")
			.send(demouser);

		const user = await User.findOne({email: demouser.email});
		expect(user.username).toBe(demouser.username);
		expect(user.email).toBe(demouser.email);
		expect(user.password).toBeTruthy();

		done();
	})

	it('second registration should fail with duplicate email', async (done) => {
		await request.post("/users/register")
			.send(demouser);

		const user = await User.findOne({email: demouser.email});
		expect(user.username).toBe(demouser.username);
		expect(user.email).toBe(demouser.email);
		expect(user.password).toBeTruthy();

		await request.post("/users/register")
			.send({
				username: "demo1",
				email: "demo@demo.com",
				password: "demodemodemo"
			});

		const user2 = await User.findOne({username: "demo1"});
		expect(user2).toBeNull();

		done();

	})

	it('should fail with no credential provided', async (done) => {
		const res = await request.post("/users/register")
			.send({username: "demo"});

		const user = await User.findOne({username: "demo"});
		expect(user).toBeNull();

		done();
	})

})
