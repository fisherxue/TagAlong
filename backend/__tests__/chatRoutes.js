const supertest = require('supertest')
const app = require("../index.js");
const mongoose = require('mongoose');
const request = supertest(app);
const User = require("../User/models/user");

const databaseName = 'chatroutestest';

let user1;
let user2;
let user3;

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


describe('testing chat', () => {

	it('should create a new room with 3 demo users with no messages', async (done) => {

		// Registration of 3 new users
		const chatdemouser1 = {
			username: "chatdemouser1",
			email: "chatdemouser1@demo.com",
			password: "demodemodemo"
		};

		const chatdemouser2 = {
			username: "chatdemouser2",
			email: "chatdemouser2@demo.com",
			password: "demodemodemo"
		};

		const chatdemouser3 = {
			username: "chatdemouser3",
			email: "chatdemouser3@demo.com",
			password: "demodemodemo"
		};


		await request.post("/users/register")
				.send(chatdemouser1);
		await request.post("/users/register")
				.send(chatdemouser2);
		await request.post("/users/register")
				.send(chatdemouser3);


		const users = [chatdemouser1.username, chatdemouser2.username, chatdemouser3.username];
		
		const res = await request.post("/chat/newRoom")
			.send({
				users: users
			})
			.expect(200);

		expect(res.body).toBeTruthy();
		expect(res.body.users).toEqual(expect.arrayContaining(users));
		expect(res.body.messages).toEqual(expect.arrayContaining([]));

		done();
	});

	it('should fail to create a chat room with no users', async (done) => {
		const res = await request.post("/chat/newRoom")
			.send()
			.expect(400);

		expect(res.text).toBe("No users supplied");

		done();
	})


	it('demo1 should send a message to the chat', async (done) => {

		// Registration of 3 new users
		const chatdemouser4 = {
			username: "chatdemouser4",
			email: "chatdemouser4@demo.com",
			password: "demodemodemo",
			fbToken: "eB2TuLLk7YA:APA91bH8JpAXQuzS8VEZlq2gGDVrbVtZS8vmU26A1wA6CUiclwylIuwwdnJeSIyi4a8PGOBfkXiBjKtRpM66lMkY_mr2a84XrnkgvSL8JEvS7ZjEgMRL0bPOKiAMf0vLFyWJBzbrMakS"
		};

		const chatdemouser5 = {
			username: "chatdemouser5",
			email: "chatdemouser5@demo.com",
			password: "demodemodemo",
			fbToken: "eB2TuLLk7YA:APA91bH8JpAXQuzS8VEZlq2gGDVrbVtZS8vmU26A1wA6CUiclwylIuwwdnJeSIyi4a8PGOBfkXiBjKtRpM66lMkY_mr2a84XrnkgvSL8JEvS7ZjEgMRL0bPOKiAMf0vLFyWJBzbrMakS"
		};

		const chatdemouser6 = {
			username: "chatdemouser6",
			email: "chatdemouser6@demo.com",
			password: "demodemodemo",
			fbToken: "eB2TuLLk7YA:APA91bH8JpAXQuzS8VEZlq2gGDVrbVtZS8vmU26A1wA6CUiclwylIuwwdnJeSIyi4a8PGOBfkXiBjKtRpM66lMkY_mr2a84XrnkgvSL8JEvS7ZjEgMRL0bPOKiAMf0vLFyWJBzbrMakS"
		};


		const user4res = await request.post("/users/register")
				.send(chatdemouser4);
		const user5res = await request.post("/users/register")
				.send(chatdemouser5);
		const user6res = await request.post("/users/register")
				.send(chatdemouser6);

		const user4 = user4res.body;
		const user5 = user5res.body;
		const user6 = user6res.body;

		const users = [chatdemouser4.username, chatdemouser5.username, chatdemouser6.username];

		// Create a new Chat Room
		const res = await request.post("/chat/newRoom")
			.send({
				users: users
			})
			.expect(200);

		const roomID = res.body._id;

		const res2 = await request.post("/chat/sendMessage")
			.send({
				userID: user4._id,
				roomID: roomID,
				message: "hello"
			})
			.expect(200); 
		
		expect(res2.body).toBeTruthy();
		expect(res2.body.users).toEqual(expect.arrayContaining(users));
		expect(res2.body.messages[0].username).toBe(user4.username);
		expect(res2.body.messages[0].message).toBe("hello");


		done();
	})

	it('sending a message to a invalid roomID', async (done) => {
		// Registration of 3 new users
		const chatdemouser7 = {
			username: "chatdemouser7",
			email: "chatdemouser7@demo.com",
			password: "demodemodemo",
			fbToken: "eB2TuLLk7YA:APA91bH8JpAXQuzS8VEZlq2gGDVrbVtZS8vmU26A1wA6CUiclwylIuwwdnJeSIyi4a8PGOBfkXiBjKtRpM66lMkY_mr2a84XrnkgvSL8JEvS7ZjEgMRL0bPOKiAMf0vLFyWJBzbrMakS"
		};

		const chatdemouser8 = {
			username: "chatdemouser8",
			email: "chatdemouser8@demo.com",
			password: "demodemodemo",
			fbToken: "eB2TuLLk7YA:APA91bH8JpAXQuzS8VEZlq2gGDVrbVtZS8vmU26A1wA6CUiclwylIuwwdnJeSIyi4a8PGOBfkXiBjKtRpM66lMkY_mr2a84XrnkgvSL8JEvS7ZjEgMRL0bPOKiAMf0vLFyWJBzbrMakS"
		};

		const chatdemouser9 = {
			username: "chatdemouser9",
			email: "chatdemouser9@demo.com",
			password: "demodemodemo",
			fbToken: "eB2TuLLk7YA:APA91bH8JpAXQuzS8VEZlq2gGDVrbVtZS8vmU26A1wA6CUiclwylIuwwdnJeSIyi4a8PGOBfkXiBjKtRpM66lMkY_mr2a84XrnkgvSL8JEvS7ZjEgMRL0bPOKiAMf0vLFyWJBzbrMakS"
		};


		const user7res = await request.post("/users/register")
				.send(chatdemouser7);
		const user8res = await request.post("/users/register")
				.send(chatdemouser8);
		const user9res = await request.post("/users/register")
				.send(chatdemouser9);

		const user7 = user7res.body;
		const user8 = user8res.body;
		const user9 = user9res.body;

		const users = [chatdemouser7.username, chatdemouser8.username, chatdemouser9.username];

		// Create a new Chat Room
		const res = await request.post("/chat/newRoom")
			.send({
				users: users
			})
			.expect(200);

		const roomID = res.body._id;

		const res2 = await request.post("/chat/sendMessage")
			.send({
				userID: user7._id,
				roomID: "0",
				message: "hello"
			})
			.expect(400); 
		
		expect(res2.text).toBe("Invalid room ID");

		done();
	})

	it('sending a message from a invalid userID', async (done) => {
		const res = await(request.post("/chat/sendMessage"))
			.send({
				userID: "0",
				roomID: "",
				message: "home"
			})
			.expect(400);

		expect(res.text).toBe("Invalid userID");

		done();

	})

	it('sending a message from a missing user, but valid userID', async (done) => {
		const res = await(request.post("/chat/sendMessage"))
			.send({
				userID: "5dd334aa82dbf7805559b74a",
				roomID: "",
				message: "home"
			})
			.expect(400);

		expect(res.text).toBe("Unable to find user");
		
		done();

	})

	it('sending a message to to a invalid roomID', async (done) => {
		
		const chatdemouser10 = {
			username: "chatdemouser10",
			email: "chatdemouser10@demo.com",
			password: "demodemodemo",
			fbToken: ""
		};

		const user10res = await request.post("/users/register")
			.send(chatdemouser10);

		const user10 = user10res.body;

		const res = await(request.post("/chat/sendMessage"))
			.send({
				userID: user10._id,
				roomID: "5dd334aa82dbf7805559b74a",
				message: "home"
			})
			.expect(400);

		expect(res.text).toBe("chat room not found");
		
		done();

	})



	it('gets message for proper user and room', async (done) => {
		const chatdemouser12 = {
			username: "chatdemouser12",
			email: "chatdemouser12@demo.com",
			password: "demodemodemo",
			fbToken: ""
		};

		const user12res = await request.post("/users/register")
			.send(chatdemouser12);

		const user12 = user12res.body;

		// Create a new Chat Room
		const res = await request.post("/chat/newRoom")
			.send({
				users: [user12.username]
			})
			.expect(200);

		const roomID = res.body._id;

		const res2 = await(request.post("/chat/sendMessage"))
			.send({
				userID: user12._id,
				roomID: roomID,
				message: "home"
			})
			.expect(200);

		const res3 = await(request.post("/chat/getMessages"))
			.send({
				userID: user12._id,
				roomID: roomID
			})
			.expect(200);

		expect(res3.body).toBeTruthy();
		expect(res3.body.users).toEqual(expect.arrayContaining([user12.username]));
		expect(res3.body.messages[0].message).toBe("home");
		expect(res3.body.messages[0].username).toBe(user12.username);

			
		done();
	})

	it('gets messages for valid userID with missing user', async (done) => {
		const res = await(request.post("/chat/getMessages"))
			.send({
				userID: "5dd334aa82dbf7805559b74a",
				roomID: ""
			})
			.expect(400);

		expect(res.text).toBe("Unable to find user");
		done();

	})

	it('attempt to get message from invalid userID', async (done) => {
		const res = await(request.post("/chat/getMessages"))
			.send({
				userID: "a",
				roomID: ""
			})
			.expect(400);

		expect(res.text).toBe("Invalid userID");
		done();
	})

	it('attempt to get message from invalid roomID but valid user', async (done) => {
		
		const chatdemouser13 = {
			username: "chatdemouser13",
			email: "chatdemouser13@demo.com",
			password: "demodemodemo",
			fbToken: ""
		};

		const user13res = await request.post("/users/register")
			.send(chatdemouser13)
			.expect(200);

		const user13 = user13res.body;

		const res = await(request.post("/chat/getMessages"))
			.send({
				userID: user13._id,
				roomID: ""
			})
			.expect(400);

		expect(res.text).toBe("Invalid roomID");
		done();


	})



})

