const supertest = require('supertest')
const app = require("../index.js");
const mongoose = require('mongoose');
const request = supertest(app);
const User = require("../User/models/user");
const Chat = require("../Chat/models/Chat");
const TripStore = require("../Trip/models/Trip");
const ridertrip1 = require("./sampletrips/ridertrip1");
const ridertrip2 = require("./sampletrips/ridertrip2");
const ridertrip3 = require("./sampletrips/ridertrip3");


const databaseName = 'integrationtest';

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

describe('integration tests', () => {
    it('registration', async (done) => {
        const demouser1 = {
            username: "demo1",
            email: "demo1@demo.com",
            password: "demodemodemo"
        }

        const res = await request.post("/users/register")
            .send(demouser1)
            .expect(200);

        const user = await User.findOne({email: demouser1.email});
        expect(user.username).toBe(demouser1.username);
        expect(user.email).toBe(demouser1.email);
        expect(user.password).toBeTruthy();
        expect(res.body).toBeTruthy();
        expect(res.body.username).toBe(demouser1.username);
        expect(res.body.email).toBe(demouser1.email);
        done();

    })

    it('registration + login', async (done) => {
        const demouser1 = {
            username: "demo1",
            email: "demo1@demo.com",
            password: "demodemodemo"
        }

        const res = await request.post("/users/register")
            .send(demouser1)
            .expect(200);

        const user = await User.findOne({email: demouser1.email});
        expect(user.username).toBe(demouser1.username);
        expect(user.email).toBe(demouser1.email);
        expect(user.password).toBeTruthy();
        expect(res.body).toBeTruthy();
        expect(res.body.username).toBe(demouser1.username);
        expect(res.body.email).toBe(demouser1.email);

        const res2 = await request.post("/users/login")
            .send({
                username: "demo1",
                password: "demodemodemo"
            })
            .expect(200);

        expect(res2.body).toBeTruthy();
        expect(res2.body.username).toBe("demo1");
        expect(res2.body.email).toBe("demo1@demo.com");
        done();
    });

    it('registration + login + update profile', async (done) => {
        const demouser1 = {
            username: "demo1",
            email: "demo1@demo.com",
            password: "demodemodemo"
        }

        const res = await request.post("/users/register")
            .send(demouser1)
            .expect(200);

        const user = await User.findOne({email: demouser1.email});
        expect(user.username).toBe(demouser1.username);
        expect(user.email).toBe(demouser1.email);
        expect(user.password).toBeTruthy();
        expect(res.body).toBeTruthy();
        expect(res.body.username).toBe(demouser1.username);
        expect(res.body.email).toBe(demouser1.email);

        const res2 = await request.post("/users/login")
            .send({
                username: "demo1",
                password: "demodemodemo"
            })
            .expect(200);

        expect(res2.body).toBeTruthy();
        expect(res2.body.username).toBe("demo1");
        expect(res2.body.email).toBe("demo1@demo.com");



        const res3 = await request.put("/users/updateProfile")
            .send({
                userID: user._id,
                interests: [0,0,0,0,0],
                firstName: "demo",
                lastName: "demo",
                age: 20,
                gender: "male",
                email: "demo@demo.com",
                carCapacity: 4,
                fbToken: "a",
                isDriver: false
            })
            .expect(200);

        expect(res3.body._id).toEqual(user._id.toString());
        expect(res3.body.firstName).toBe("demo");
        expect(res3.body.lastName).toBe("demo");
        expect(res3.body.age).toBe(20);
        expect(res3.body.gender).toBe("male");
        expect(res3.body.email).toBe("demo@demo.com");
        expect(res3.body.carCapacity).toBe(4);
        expect(res3.body.fbToken).toBe("a");
        expect(res3.body.isDriver).toBe(false);

        done();
    });

    it('registration + login + update profile + newTrip', async (done) => {
        const demouser1 = {
            username: "demo1",
            email: "demo1@demo.com",
            password: "demodemodemo"
        }

        const res = await request.post("/users/register")
            .send(demouser1)
            .expect(200);

        const user = await User.findOne({email: demouser1.email});
        expect(user.username).toBe(demouser1.username);
        expect(user.email).toBe(demouser1.email);
        expect(user.password).toBeTruthy();
        expect(res.body).toBeTruthy();
        expect(res.body.username).toBe(demouser1.username);
        expect(res.body.email).toBe(demouser1.email);

        const res2 = await request.post("/users/login")
            .send({
                username: "demo1",
                password: "demodemodemo"
            })
            .expect(200);

        expect(res2.body).toBeTruthy();
        expect(res2.body.username).toBe("demo1");
        expect(res2.body.email).toBe("demo1@demo.com");



        const res3 = await request.put("/users/updateProfile")
            .send({
                userID: user._id,
                interests: [0,0,0,0,0],
                firstName: "demo",
                lastName: "demo",
                age: 20,
                gender: "male",
                email: "demo@demo.com",
                carCapacity: 4,
                fbToken: "a",
                isDriver: false
            })
            .expect(200);

        expect(res3.body._id).toEqual(user._id.toString());
        expect(res3.body.firstName).toBe("demo");
        expect(res3.body.lastName).toBe("demo");
        expect(res3.body.age).toBe(20);
        expect(res3.body.gender).toBe("male");
        expect(res3.body.email).toBe("demo@demo.com");
        expect(res3.body.carCapacity).toBe(4);
        expect(res3.body.fbToken).toBe("a");
        expect(res3.body.isDriver).toBe(false);

        const res4 = await request.post("/trips/newTrip")
            .send({ 
               "arrivalPlace":"not assigned",
               "arrivalTime":"Nov 29, 2019 08:00:00",
               "departurePlace":"not assigned",
               "departureTime":"Nov 18, 2019 23:46:11",
               "isDriverTrip":false,
               "roomID":"not assigned",
               "taggedUsers":[ 
                  "not assigned"
               ],
               "tripID":"not assigned",
               "tripRoute":{ 
                  "nameValuePairs":{ 
                     "origin":"49.2621563,-123.2501125",
                     "destination":"49.21278372465756,-123.15078578889369"
                  }
               },
               "userID": user._id,
               "username": user.username
            })
            .expect(200);

        expect(res4.body).toBeTruthy();
        expect(res4.body.isFulfilled).toBe(false);
        expect(res4.body.tripRoute).toBeTruthy();
        expect(res4.body.tripRoute.status).toBe("OK");
        expect(res4.body.isDriverTrip).toBe(false);

        done();
    });

    it('registration + login + update profile + newTrip + Get Trips', async (done) => {
        const demouser1 = {
            username: "demo1",
            email: "demo1@demo.com",
            password: "demodemodemo"
        }

        const res = await request.post("/users/register")
            .send(demouser1)
            .expect(200);

        const user = await User.findOne({email: demouser1.email});
        expect(user.username).toBe(demouser1.username);
        expect(user.email).toBe(demouser1.email);
        expect(user.password).toBeTruthy();
        expect(res.body).toBeTruthy();
        expect(res.body.username).toBe(demouser1.username);
        expect(res.body.email).toBe(demouser1.email);

        const res2 = await request.post("/users/login")
            .send({
                username: "demo1",
                password: "demodemodemo"
            })
            .expect(200);

        expect(res2.body).toBeTruthy();
        expect(res2.body.username).toBe("demo1");
        expect(res2.body.email).toBe("demo1@demo.com");



        const res3 = await request.put("/users/updateProfile")
            .send({
                userID: user._id,
                interests: [0,0,0,0,0],
                firstName: "demo",
                lastName: "demo",
                age: 20,
                gender: "male",
                email: "demo@demo.com",
                carCapacity: 4,
                fbToken: "a",
                isDriver: false
            })
            .expect(200);

        expect(res3.body._id).toEqual(user._id.toString());
        expect(res3.body.firstName).toBe("demo");
        expect(res3.body.lastName).toBe("demo");
        expect(res3.body.age).toBe(20);
        expect(res3.body.gender).toBe("male");
        expect(res3.body.email).toBe("demo@demo.com");
        expect(res3.body.carCapacity).toBe(4);
        expect(res3.body.fbToken).toBe("a");
        expect(res3.body.isDriver).toBe(false);

        const res4 = await request.post("/trips/newTrip")
            .send({ 
               "arrivalPlace":"not assigned",
               "arrivalTime":"Nov 29, 2019 08:00:00",
               "departurePlace":"not assigned",
               "departureTime":"Nov 18, 2019 23:46:11",
               "isDriverTrip":false,
               "roomID":"not assigned",
               "taggedUsers":[ 
                  "not assigned"
               ],
               "tripID":"not assigned",
               "tripRoute":{ 
                  "nameValuePairs":{ 
                     "origin":"49.2621563,-123.2501125",
                     "destination":"49.21278372465756,-123.15078578889369"
                  }
               },
               "userID": user._id,
               "username": user.username
            })
            .expect(200);

        expect(res4.body).toBeTruthy();
        expect(res4.body.isFulfilled).toBe(false);
        expect(res4.body.tripRoute).toBeTruthy();
        expect(res4.body.tripRoute.status).toBe("OK");
        expect(res4.body.isDriverTrip).toBe(false);

        const res5 = await request.post("/trips/myTrips")
            .send({
                userID: user._id
            })
            .expect(200);

        expect(res5.body).toBeTruthy();
        expect(res5.body.trips).toHaveLength(1);

        done();
    })

    it('registration + login + update profile + newTrip + Get Trips + DelTrip', async (done) => {
        const demouser1 = {
            username: "demo1",
            email: "demo1@demo.com",
            password: "demodemodemo"
        }

        const res = await request.post("/users/register")
            .send(demouser1)
            .expect(200);

        const user = await User.findOne({email: demouser1.email});
        expect(user.username).toBe(demouser1.username);
        expect(user.email).toBe(demouser1.email);
        expect(user.password).toBeTruthy();
        expect(res.body).toBeTruthy();
        expect(res.body.username).toBe(demouser1.username);
        expect(res.body.email).toBe(demouser1.email);

        const res2 = await request.post("/users/login")
            .send({
                username: "demo1",
                password: "demodemodemo"
            })
            .expect(200);

        expect(res2.body).toBeTruthy();
        expect(res2.body.username).toBe("demo1");
        expect(res2.body.email).toBe("demo1@demo.com");



        const res3 = await request.put("/users/updateProfile")
            .send({
                userID: user._id,
                interests: [0,0,0,0,0],
                firstName: "demo",
                lastName: "demo",
                age: 20,
                gender: "male",
                email: "demo@demo.com",
                carCapacity: 4,
                fbToken: "a",
                isDriver: false
            })
            .expect(200);

        expect(res3.body._id).toEqual(user._id.toString());
        expect(res3.body.firstName).toBe("demo");
        expect(res3.body.lastName).toBe("demo");
        expect(res3.body.age).toBe(20);
        expect(res3.body.gender).toBe("male");
        expect(res3.body.email).toBe("demo@demo.com");
        expect(res3.body.carCapacity).toBe(4);
        expect(res3.body.fbToken).toBe("a");
        expect(res3.body.isDriver).toBe(false);

        const res4 = await request.post("/trips/newTrip")
            .send({ 
               "arrivalPlace":"not assigned",
               "arrivalTime":"Nov 29, 2019 08:00:00",
               "departurePlace":"not assigned",
               "departureTime":"Nov 18, 2019 23:46:11",
               "isDriverTrip":false,
               "roomID":"not assigned",
               "taggedUsers":[ 
                  "not assigned"
               ],
               "tripID":"not assigned",
               "tripRoute":{ 
                  "nameValuePairs":{ 
                     "origin":"49.2621563,-123.2501125",
                     "destination":"49.21278372465756,-123.15078578889369"
                  }
               },
               "userID": user._id,
               "username": user.username
            })
            .expect(200);

        expect(res4.body).toBeTruthy();
        expect(res4.body.isFulfilled).toBe(false);
        expect(res4.body.tripRoute).toBeTruthy();
        expect(res4.body.tripRoute.status).toBe("OK");
        expect(res4.body.isDriverTrip).toBe(false);

        const res5 = await request.post("/trips/myTrips")
            .send({
                userID: user._id
            })
            .expect(200);

        expect(res5.body).toBeTruthy();
        expect(res5.body.trips).toHaveLength(1);


        const res6 = await request.del("/trips/delTrip")
            .send({
                userID: user._id,
                tripID: res5.body.trips[0]._id
            })
            .expect(200);

        expect(res6.body).toBe("trip successfully deleted");

        done();
    });

    it('registration + login + update profile + newTrip + Get Trips + DelTrip + Chat', async (done) => {
        const demouser1 = {
            username: "demo1",
            email: "demo1@demo.com",
            password: "demodemodemo"
        }

        const res = await request.post("/users/register")
            .send(demouser1)
            .expect(200);

        const user = await User.findOne({email: demouser1.email});
        expect(user.username).toBe(demouser1.username);
        expect(user.email).toBe(demouser1.email);
        expect(user.password).toBeTruthy();
        expect(res.body).toBeTruthy();
        expect(res.body.username).toBe(demouser1.username);
        expect(res.body.email).toBe(demouser1.email);

        const res2 = await request.post("/users/login")
            .send({
                username: "demo1",
                password: "demodemodemo"
            })
            .expect(200);

        expect(res2.body).toBeTruthy();
        expect(res2.body.username).toBe("demo1");
        expect(res2.body.email).toBe("demo1@demo.com");



        const res3 = await request.put("/users/updateProfile")
            .send({
                userID: user._id,
                interests: [0,0,0,0,0],
                firstName: "demo",
                lastName: "demo",
                age: 20,
                gender: "male",
                email: "demo@demo.com",
                carCapacity: 4,
                fbToken: "a",
                isDriver: false
            })
            .expect(200);

        expect(res3.body._id).toEqual(user._id.toString());
        expect(res3.body.firstName).toBe("demo");
        expect(res3.body.lastName).toBe("demo");
        expect(res3.body.age).toBe(20);
        expect(res3.body.gender).toBe("male");
        expect(res3.body.email).toBe("demo@demo.com");
        expect(res3.body.carCapacity).toBe(4);
        expect(res3.body.fbToken).toBe("a");
        expect(res3.body.isDriver).toBe(false);

        const res4 = await request.post("/trips/newTrip")
            .send({ 
               "arrivalPlace":"not assigned",
               "arrivalTime":"Nov 29, 2019 08:00:00",
               "departurePlace":"not assigned",
               "departureTime":"Nov 18, 2019 23:46:11",
               "isDriverTrip":false,
               "roomID":"not assigned",
               "taggedUsers":[ 
                  "not assigned"
               ],
               "tripID":"not assigned",
               "tripRoute":{ 
                  "nameValuePairs":{ 
                     "origin":"49.2621563,-123.2501125",
                     "destination":"49.21278372465756,-123.15078578889369"
                  }
               },
               "userID": user._id,
               "username": user.username
            })
            .expect(200);

        expect(res4.body).toBeTruthy();
        expect(res4.body.isFulfilled).toBe(false);
        expect(res4.body.tripRoute).toBeTruthy();
        expect(res4.body.tripRoute.status).toBe("OK");
        expect(res4.body.isDriverTrip).toBe(false);

        const res5 = await request.post("/trips/myTrips")
            .send({
                userID: user._id
            })
            .expect(200);

        expect(res5.body).toBeTruthy();
        expect(res5.body.trips).toHaveLength(1);


        const res6 = await request.del("/trips/delTrip")
            .send({
                userID: user._id,
                tripID: res5.body.trips[0]._id
            })
            .expect(200);

        expect(res6.body).toBe("trip successfully deleted");

        const demouser2 = {
            username: "demo2",
            email: "demo2@demo.com",
            password: "demodemodemo"
        }

        const res7 = await request.post("/users/register")
            .send(demouser2)
            .expect(200);

        const user2 = await User.findOne({email: demouser2.email});
        expect(user2.username).toBe(demouser2.username);
        expect(user2.email).toBe(demouser2.email);
        expect(user2.password).toBeTruthy();
        expect(res7.body).toBeTruthy();
        expect(res7.body.username).toBe(demouser2.username);
        expect(res7.body.email).toBe(demouser2.email);

        const res8 = await request.post("/chat/newRoom")
            .send({
                users: [user.username, user2.username]
            })

        expect(res8.body).toBeTruthy();
        expect(res8.body.users).toEqual(expect.arrayContaining([user.username, user2.username]));
        expect(res8.body.messages).toEqual(expect.arrayContaining([]));

        const res9 = await request.post("/chat/sendMessage")
            .send({
                userID: user._id,
                roomID: res8.body._id,
                message: "hello"
            })
            .expect(200); 
        
        expect(res9.body).toBeTruthy();
        expect(res9.body.users).toEqual(expect.arrayContaining([user.username, user2.username]));
        expect(res9.body.messages[0].username).toBe(user.username);
        expect(res9.body.messages[0].message).toBe("hello");

        const res10 = await request.post("/chat/sendMessage")
            .send({
                userID: user2._id,
                roomID: res8.body._id,
                message: "hello"
            })
            .expect(200); 
        
        expect(res10.body).toBeTruthy();
        expect(res10.body.users).toEqual(expect.arrayContaining([user.username, user2.username]));
        expect(res10.body.messages[0].username).toBe(user.username);
        expect(res10.body.messages[0].message).toBe("hello");


        done();
    });


})



