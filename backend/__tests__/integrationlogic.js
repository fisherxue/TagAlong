const supertest = require('supertest')
const app = require("../index.js");
const mongoose = require('mongoose');
const request = supertest(app);
const TripStore = require("../Trip/models/Trip");
const bcrypt = require("bcryptjs");
const User = require("../User/models/user");
const ridertrip1 = require("./sampletrips/ridertrip1");
const ridertrip2 = require("./sampletrips/ridertrip2");
const ridertrip3 = require("./sampletrips/ridertrip3");



const databaseName = 'integrationtest'

beforeAll(async () => {
	const url = `mongodb://127.0.0.1/${databaseName}`;
  	await mongoose.connect(url, { useNewUrlParser: true, useUnifiedTopology: true })
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

describe ("testing integration", () => {
    it("should succeed with logic test", async done => {
        const demouser1 = {
			username: "demo1",
			email: "demo1@demo.com",
			password: "demodemodemo"
		}

		let res = await request.post("/users/register")
			.send(demouser1)
			.expect(200);

		const user = await User.findOne({email: demouser1.email});
		expect(user.username).toBe(demouser1.username);
		expect(user.email).toBe(demouser1.email);
		expect(user.password).toBeTruthy();
		expect(res.body).toBeTruthy();
		expect(res.body.username).toBe(demouser1.username);
		expect(res.body.email).toBe(demouser1.email);
		user1 = new User({
            _id: "5dd36a75b458db53e031818e",
            username: "bwong",
            email: "bwong@demo.com",
            password: "demodemodemo",
            fbToken: "",
            isDriverTrip: false,
            interests: [5,5,5,5,5]
        });

        await user1.save();

        user2 = new User({
            _id: "5dd36c81b458db53e031819b",
            username: "bwong5",
            email: "bwong5@demo.com",
            password: "demodemodemo",
            fbToken: "",
            isDriverTrip: false,
            interests: [5,5,5,5,5]

        });

        await user2.save();

        user3 = new User({
            _id: "5dd36b0fb458db53e0318192",
            username: "bwong2",
            email: "bwong2@demo.com",
            password: "demodemodemo",
            fbToken: "",
            isDriverTrip: false,
            interests: [5,5,5,5,5]

        });

        await user3.save();

        let driver;

        user4 = new User({
            username: "bwong1",
            email: "bwong1@demo.com",
            password: "demodemodemo",
            fbToken: "",
            isDriverTrip: true,
            interests: [5,5,5,5,5]
        });

        await user4.save().then((user) => {
            driver = user;
        });


        riderTrip1 = new TripStore(ridertrip1);
        await riderTrip1.save();
        riderTrip2 = new TripStore(ridertrip2);
        await riderTrip2.save();
        riderTrip3 = new TripStore(ridertrip3);
        await riderTrip3.save();

        res = await request.post("/trips/newTrip")
            .send({
               "arrivalPlace":"not assigned",
               "arrivalTime":"Nov 26, 2019 10:20:00",
               "departurePlace":"not assigned",
               "departureTime":"Nov 19, 2019 00:42:59",
               "isDriverTrip":true,
               "roomID":"not assigned",
               "taggedUsers":[
                  "not assigned"
               ],
               "tripID":"not assigned",
               "tripRoute":{
                  "nameValuePairs":{
                     "origin":"49.262158,-123.2500847",
                     "destination":"49.28284681397363,-123.12324184924364"
                  }
               },
               "userID": driver._id,
               "username": driver.username
            }).expect(200);

		expect(res.body.taggedUsers).toEqual(expect.arrayContaining(["bwong", "bwong5", "bwong2"]));

		res = await request.post("/trips/myTrips")
            .send({
                userID: user1._id
            })
            .expect(200);

        expect(res.body).toBeTruthy();
        expect(res.body.trips).toHaveLength(1);
		done();
    });
});