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
    it('RIDER: registration', async (done) => {
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

    it('RIDER: registration + login', async (done) => {
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


    it('RIDER: registration + login + update profile', async (done) => {
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

    it('RIDER: registration + login + update profile + newTrip', async (done) => {
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
               "arrivalTime":"Nov 27, 2019 00:00:00",
               "departurePlace":"not assigned",
               "departureTime":"Nov 26, 2019 00:44:08",
               "isDriverTrip": false,
               "roomID":"not assigned",
               "taggedUsers":[ 
                  "not assigned"
               ],
               "tripID":"not assigned",
               "tripRoute":{ 
                  "nameValuePairs":{ 
                     "origin":"49.262141,-123.2500775",
                     "destination":"49.2614434,-123.25669599999998"
                  }
               },
               "userID": user._id,
               "username":"demo1"
            })
            .expect(200);

        expect(res4.body).toBeTruthy();
        expect(res4.body.username).toBe("demo1");
        expect(res4.body.tripRoute.status).toBe("OK");
        done();
    });


    it('DRIVER: registration + login + update profile + newTrip', async (done) => {
        const demouser2 = {
            username: "demo2",
            email: "demo2@demo.com",
            password: "demodemodemo"
        }

        const res = await request.post("/users/register")
            .send(demouser2)
            .expect(200);

        const user = await User.findOne({email: demouser2.email});
        expect(user.username).toBe(demouser2.username);
        expect(user.email).toBe(demouser2.email);
        expect(user.password).toBeTruthy();
        expect(res.body).toBeTruthy();
        expect(res.body.username).toBe(demouser2.username);
        expect(res.body.email).toBe(demouser2.email);

        const res2 = await request.post("/users/login")
            .send({
                username: "demo2",
                password: "demodemodemo"
            })
            .expect(200);

        expect(res2.body).toBeTruthy();
        expect(res2.body.username).toBe("demo2");
        expect(res2.body.email).toBe("demo2@demo.com");



        const res3 = await request.put("/users/updateProfile")
            .send({
                userID: user._id,
                interests: [0,0,0,0,0],
                firstName: "demo2",
                lastName: "demo2",
                age: 20,
                gender: "male",
                email: "demo2@demo.com",
                carCapacity: 4,
                fbToken: "a",
                isDriver: true
            })
            .expect(200);

        expect(res3.body._id).toEqual(user._id.toString());
        expect(res3.body.firstName).toBe("demo2");
        expect(res3.body.lastName).toBe("demo2");
        expect(res3.body.age).toBe(20);
        expect(res3.body.gender).toBe("male");
        expect(res3.body.email).toBe("demo2@demo.com");
        expect(res3.body.carCapacity).toBe(4);
        expect(res3.body.fbToken).toBe("a");
        expect(res3.body.isDriver).toBe(true);

        const res4 = await request.post("/trips/newTrip")
            .send({ 
               "arrivalPlace":"not assigned",
               "arrivalTime":"Nov 27, 2019 00:00:00",
               "departurePlace":"not assigned",
               "departureTime":"Nov 26, 2019 00:44:08",
               "isDriverTrip": false,
               "roomID":"not assigned",
               "taggedUsers":[ 
                  "not assigned"
               ],
               "tripID":"not assigned",
               "tripRoute":{ 
                  "nameValuePairs":{ 
                     "origin":"49.262141,-123.2500775",
                     "destination":"49.2614434,-123.25669599999998"
                  }
               },
               "userID": user._id,
               "username":"demo2"
            })
            .expect(200);

        expect(res4.body).toBeTruthy();
        expect(res4.body.username).toBe("demo2");
        expect(res4.body.tripRoute.status).toBe("OK");

        done();
    });

    it('RIDER AND DRIVER: registration + login + update profile + newTrip + DRIVER GET RECOMMENDEDTRIPS', async (done) => {
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
               "arrivalTime":"Nov 27, 2019 00:00:00",
               "departurePlace":"not assigned",
               "departureTime":"Nov 26, 2019 00:44:08",
               "isDriverTrip": false,
               "roomID":"not assigned",
               "taggedUsers":[ 
                  "not assigned"
               ],
               "tripID":"not assigned",
               "tripRoute":{ 
                  "nameValuePairs":{ 
                     "origin":"49.262141,-123.2500775",
                     "destination":"49.2614434,-123.25669599999998"
                  }
               },
               "userID": user._id,
               "username":"demo1"
            })
            .expect(200);

        expect(res4.body).toBeTruthy();
        expect(res4.body.username).toBe("demo1");
        expect(res4.body.tripRoute.status).toBe("OK");


        const demouser2 = {
            username: "demo2",
            email: "demo2@demo.com",
            password: "demodemodemo"
        }

        const res5 = await request.post("/users/register")
            .send(demouser2)
            .expect(200);

        const user2 = await User.findOne({email: demouser2.email});
        expect(user2.username).toBe(demouser2.username);
        expect(user2.email).toBe(demouser2.email);
        expect(user2.password).toBeTruthy();
        expect(res5.body).toBeTruthy();
        expect(res5.body.username).toBe(demouser2.username);
        expect(res5.body.email).toBe(demouser2.email);

        const res6 = await request.post("/users/login")
            .send({
                username: "demo2",
                password: "demodemodemo"
            })
            .expect(200);

        expect(res6.body).toBeTruthy();
        expect(res6.body.username).toBe("demo2");
        expect(res6.body.email).toBe("demo2@demo.com");



        const res7 = await request.put("/users/updateProfile")
            .send({
                userID: user2._id,
                interests: [0,0,0,0,0],
                firstName: "demo2",
                lastName: "demo2",
                age: 20,
                gender: "male",
                email: "demo2@demo.com",
                carCapacity: 4,
                fbToken: "a",
                isDriver: true
            })
            .expect(200);

        expect(res7.body._id).toEqual(user2._id.toString());
        expect(res7.body.firstName).toBe("demo2");
        expect(res7.body.lastName).toBe("demo2");
        expect(res7.body.age).toBe(20);
        expect(res7.body.gender).toBe("male");
        expect(res7.body.email).toBe("demo2@demo.com");
        expect(res7.body.carCapacity).toBe(4);
        expect(res7.body.fbToken).toBe("a");
        expect(res7.body.isDriver).toBe(true);

        const res8 = await request.post("/trips/newTrip")
            .send({ 
               "arrivalPlace":"not assigned",
               "arrivalTime":"Nov 27, 2019 00:00:00",
               "departurePlace":"not assigned",
               "departureTime":"Nov 26, 2019 00:44:08",
               "isDriverTrip": true,
               "roomID":"not assigned",
               "taggedUsers":[ 
                  "not assigned"
               ],
               "tripID":"not assigned",
               "tripRoute":{ 
                  "nameValuePairs":{ 
                     "origin":"49.262141,-123.2500775",
                     "destination":"49.2614434,-123.25669599999998"
                  }
               },
               "userID": user2._id,
               "username":"demo2"
            })
            .expect(200);

        expect(res8.body).toBeTruthy();
        expect(res8.body.username).toBe("demo2");
        expect(res8.body.tripRoute.status).toBe("OK");

        const res9 = await request.get("/trips/getRecommendedTrips")
            .set({
                userid: user2._id
            })
            .expect(200);

        expect(res9.body).toBeTruthy;
        expect(res9.body.trips).toHaveLength(1);
        expect(res9.body.trips[0].riderTrips).toHaveLength(1);

        done();
    });

    it('RIDER AND DRIVER: registration + login + update profile + newTrip + DRIVER GET RECOMMENDEDTRIPS + DRIVER ACCEPTING TRIP', async (done) => {
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
               "arrivalTime":"Nov 27, 2019 00:00:00",
               "departurePlace":"not assigned",
               "departureTime":"Nov 26, 2019 00:44:08",
               "isDriverTrip": false,
               "roomID":"not assigned",
               "taggedUsers":[ 
                  "not assigned"
               ],
               "tripID":"not assigned",
               "tripRoute":{ 
                  "nameValuePairs":{ 
                     "origin":"49.262141,-123.2500775",
                     "destination":"49.2614434,-123.25669599999998"
                  }
               },
               "userID": user._id,
               "username":"demo1"
            })
            .expect(200);

        expect(res4.body).toBeTruthy();
        expect(res4.body.username).toBe("demo1");
        expect(res4.body.tripRoute.status).toBe("OK");


        const demouser2 = {
            username: "demo2",
            email: "demo2@demo.com",
            password: "demodemodemo"
        }

        const res5 = await request.post("/users/register")
            .send(demouser2)
            .expect(200);

        const user2 = await User.findOne({email: demouser2.email});
        expect(user2.username).toBe(demouser2.username);
        expect(user2.email).toBe(demouser2.email);
        expect(user2.password).toBeTruthy();
        expect(res5.body).toBeTruthy();
        expect(res5.body.username).toBe(demouser2.username);
        expect(res5.body.email).toBe(demouser2.email);

        const res6 = await request.post("/users/login")
            .send({
                username: "demo2",
                password: "demodemodemo"
            })
            .expect(200);

        expect(res6.body).toBeTruthy();
        expect(res6.body.username).toBe("demo2");
        expect(res6.body.email).toBe("demo2@demo.com");



        const res7 = await request.put("/users/updateProfile")
            .send({
                userID: user2._id,
                interests: [0,0,0,0,0],
                firstName: "demo2",
                lastName: "demo2",
                age: 20,
                gender: "male",
                email: "demo2@demo.com",
                carCapacity: 4,
                fbToken: "a",
                isDriver: true
            })
            .expect(200);

        expect(res7.body._id).toEqual(user2._id.toString());
        expect(res7.body.firstName).toBe("demo2");
        expect(res7.body.lastName).toBe("demo2");
        expect(res7.body.age).toBe(20);
        expect(res7.body.gender).toBe("male");
        expect(res7.body.email).toBe("demo2@demo.com");
        expect(res7.body.carCapacity).toBe(4);
        expect(res7.body.fbToken).toBe("a");
        expect(res7.body.isDriver).toBe(true);

        const res8 = await request.post("/trips/newTrip")
            .send({ 
               "arrivalPlace":"not assigned",
               "arrivalTime":"Nov 27, 2019 00:00:00",
               "departurePlace":"not assigned",
               "departureTime":"Nov 26, 2019 00:44:08",
               "isDriverTrip": true,
               "roomID":"not assigned",
               "taggedUsers":[ 
                  "not assigned"
               ],
               "tripID":"not assigned",
               "tripRoute":{ 
                  "nameValuePairs":{ 
                     "origin":"49.262141,-123.2500775",
                     "destination":"49.2614434,-123.25669599999998"
                  }
               },
               "userID": user2._id,
               "username":"demo2"
            })
            .expect(200);

        expect(res8.body).toBeTruthy();
        expect(res8.body.username).toBe("demo2");
        expect(res8.body.tripRoute.status).toBe("OK");

        const res9 = await request.get("/trips/getRecommendedTrips")
            .set({
                userid: user2._id
            })
            .expect(200);

        expect(res9.body).toBeTruthy;
        expect(res9.body.trips).toHaveLength(1);
        expect(res9.body.trips[0].riderTrips).toHaveLength(1);

        const res10 = await request.post("/trips/acceptTrip")
            .send({
               userID: user2._id,
               tripID: res9.body.trips[0].drivertrip._id,
               usertripID: res9.body.trips[0].riderTrips[0]._id
            })
            .expect(200);

        expect(res10.body.status).toBe("OK");
        expect(res10.body.message).toBe("user successfully added to trip");

        const taggedusertrip = await TripStore.findById(res9.body.trips[0].riderTrips[0]._id);
        const taggeddrivertrip = await TripStore.findById(res9.body.trips[0].drivertrip._id);

        expect(taggedusertrip.taggedUsers).toEqual(expect.arrayContaining(["demo2"]));
        expect(taggedusertrip.chatroomid).toEqual(taggeddrivertrip.chatroomid);
        expect(taggedusertrip.isFulfilled).toEqual(true);


        done();
    });


    it('RIDER AND DRIVER: registration + login + update profile + newTrip + DRIVER GET RECOMMENDEDTRIPS + DRIVER ACCEPTING TRIP + DRIVER CHAT', async (done) => {
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
               "arrivalTime":"Nov 27, 2019 00:00:00",
               "departurePlace":"not assigned",
               "departureTime":"Nov 26, 2019 00:44:08",
               "isDriverTrip": false,
               "roomID":"not assigned",
               "taggedUsers":[ 
                  "not assigned"
               ],
               "tripID":"not assigned",
               "tripRoute":{ 
                  "nameValuePairs":{ 
                     "origin":"49.262141,-123.2500775",
                     "destination":"49.2614434,-123.25669599999998"
                  }
               },
               "userID": user._id,
               "username":"demo1"
            })
            .expect(200);

        expect(res4.body).toBeTruthy();
        expect(res4.body.username).toBe("demo1");
        expect(res4.body.tripRoute.status).toBe("OK");


        const demouser2 = {
            username: "demo2",
            email: "demo2@demo.com",
            password: "demodemodemo"
        }

        const res5 = await request.post("/users/register")
            .send(demouser2)
            .expect(200);

        const user2 = await User.findOne({email: demouser2.email});
        expect(user2.username).toBe(demouser2.username);
        expect(user2.email).toBe(demouser2.email);
        expect(user2.password).toBeTruthy();
        expect(res5.body).toBeTruthy();
        expect(res5.body.username).toBe(demouser2.username);
        expect(res5.body.email).toBe(demouser2.email);

        const res6 = await request.post("/users/login")
            .send({
                username: "demo2",
                password: "demodemodemo"
            })
            .expect(200);

        expect(res6.body).toBeTruthy();
        expect(res6.body.username).toBe("demo2");
        expect(res6.body.email).toBe("demo2@demo.com");



        const res7 = await request.put("/users/updateProfile")
            .send({
                userID: user2._id,
                interests: [0,0,0,0,0],
                firstName: "demo2",
                lastName: "demo2",
                age: 20,
                gender: "male",
                email: "demo2@demo.com",
                carCapacity: 4,
                fbToken: "a",
                isDriver: true
            })
            .expect(200);

        expect(res7.body._id).toEqual(user2._id.toString());
        expect(res7.body.firstName).toBe("demo2");
        expect(res7.body.lastName).toBe("demo2");
        expect(res7.body.age).toBe(20);
        expect(res7.body.gender).toBe("male");
        expect(res7.body.email).toBe("demo2@demo.com");
        expect(res7.body.carCapacity).toBe(4);
        expect(res7.body.fbToken).toBe("a");
        expect(res7.body.isDriver).toBe(true);

        const res8 = await request.post("/trips/newTrip")
            .send({ 
               "arrivalPlace":"not assigned",
               "arrivalTime":"Nov 27, 2019 00:00:00",
               "departurePlace":"not assigned",
               "departureTime":"Nov 26, 2019 00:44:08",
               "isDriverTrip": true,
               "roomID":"not assigned",
               "taggedUsers":[ 
                  "not assigned"
               ],
               "tripID":"not assigned",
               "tripRoute":{ 
                  "nameValuePairs":{ 
                     "origin":"49.262141,-123.2500775",
                     "destination":"49.2614434,-123.25669599999998"
                  }
               },
               "userID": user2._id,
               "username":"demo2"
            })
            .expect(200);

        expect(res8.body).toBeTruthy();
        expect(res8.body.username).toBe("demo2");
        expect(res8.body.tripRoute.status).toBe("OK");

        const res9 = await request.get("/trips/getRecommendedTrips")
            .set({
                userid: user2._id
            })
            .expect(200);

        expect(res9.body).toBeTruthy;
        expect(res9.body.trips).toHaveLength(1);
        expect(res9.body.trips[0].riderTrips).toHaveLength(1);

        const res10 = await request.post("/trips/acceptTrip")
            .send({
               userID: user2._id,
               tripID: res9.body.trips[0].drivertrip._id,
               usertripID: res9.body.trips[0].riderTrips[0]._id
            })
            .expect(200);

        expect(res10.body.status).toBe("OK");
        expect(res10.body.message).toBe("user successfully added to trip");

        const taggedusertrip = await TripStore.findById(res9.body.trips[0].riderTrips[0]._id);
        const taggeddrivertrip = await TripStore.findById(res9.body.trips[0].drivertrip._id);

        expect(taggedusertrip.taggedUsers).toEqual(expect.arrayContaining(["demo2"]));
        expect(taggedusertrip.chatroomID).toEqual(taggeddrivertrip.chatroomID);
        expect(taggedusertrip.isFulfilled).toEqual(true);

        const res11 = await request.post("/chat/sendMessage")
            .send({
                userID: user2._id,
                roomID: taggedusertrip.chatroomID,
                message: "hello"
            })
            .expect(200);


        
        expect(res11.body.messages).toHaveLength(1);
        expect(res11.body.messages[0].message).toBe("hello");
        expect(res11.body.messages[0].username).toBe("demo2");
        expect(res11.body.users).toEqual(expect.arrayContaining(["demo2", "demo1"]));

        done();
    });

    it('RIDER AND DRIVER: registration + login + update profile + newTrip + DRIVER GET RECOMMENDEDTRIPS + DRIVER ACCEPTING TRIP + DRIVER CHAT + RIDER GET CHAT ', async (done) => {
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
               "arrivalTime":"Nov 27, 2019 00:00:00",
               "departurePlace":"not assigned",
               "departureTime":"Nov 26, 2019 00:44:08",
               "isDriverTrip": false,
               "roomID":"not assigned",
               "taggedUsers":[ 
                  "not assigned"
               ],
               "tripID":"not assigned",
               "tripRoute":{ 
                  "nameValuePairs":{ 
                     "origin":"49.262141,-123.2500775",
                     "destination":"49.2614434,-123.25669599999998"
                  }
               },
               "userID": user._id,
               "username":"demo1"
            })
            .expect(200);

        expect(res4.body).toBeTruthy();
        expect(res4.body.username).toBe("demo1");
        expect(res4.body.tripRoute.status).toBe("OK");


        const demouser2 = {
            username: "demo2",
            email: "demo2@demo.com",
            password: "demodemodemo"
        }

        const res5 = await request.post("/users/register")
            .send(demouser2)
            .expect(200);

        const user2 = await User.findOne({email: demouser2.email});
        expect(user2.username).toBe(demouser2.username);
        expect(user2.email).toBe(demouser2.email);
        expect(user2.password).toBeTruthy();
        expect(res5.body).toBeTruthy();
        expect(res5.body.username).toBe(demouser2.username);
        expect(res5.body.email).toBe(demouser2.email);

        const res6 = await request.post("/users/login")
            .send({
                username: "demo2",
                password: "demodemodemo"
            })
            .expect(200);

        expect(res6.body).toBeTruthy();
        expect(res6.body.username).toBe("demo2");
        expect(res6.body.email).toBe("demo2@demo.com");



        const res7 = await request.put("/users/updateProfile")
            .send({
                userID: user2._id,
                interests: [0,0,0,0,0],
                firstName: "demo2",
                lastName: "demo2",
                age: 20,
                gender: "male",
                email: "demo2@demo.com",
                carCapacity: 4,
                fbToken: "a",
                isDriver: true
            })
            .expect(200);

        expect(res7.body._id).toEqual(user2._id.toString());
        expect(res7.body.firstName).toBe("demo2");
        expect(res7.body.lastName).toBe("demo2");
        expect(res7.body.age).toBe(20);
        expect(res7.body.gender).toBe("male");
        expect(res7.body.email).toBe("demo2@demo.com");
        expect(res7.body.carCapacity).toBe(4);
        expect(res7.body.fbToken).toBe("a");
        expect(res7.body.isDriver).toBe(true);

        const res8 = await request.post("/trips/newTrip")
            .send({ 
               "arrivalPlace":"not assigned",
               "arrivalTime":"Nov 27, 2019 00:00:00",
               "departurePlace":"not assigned",
               "departureTime":"Nov 26, 2019 00:44:08",
               "isDriverTrip": true,
               "roomID":"not assigned",
               "taggedUsers":[ 
                  "not assigned"
               ],
               "tripID":"not assigned",
               "tripRoute":{ 
                  "nameValuePairs":{ 
                     "origin":"49.262141,-123.2500775",
                     "destination":"49.2614434,-123.25669599999998"
                  }
               },
               "userID": user2._id,
               "username":"demo2"
            })
            .expect(200);

        expect(res8.body).toBeTruthy();
        expect(res8.body.username).toBe("demo2");
        expect(res8.body.tripRoute.status).toBe("OK");

        const res9 = await request.get("/trips/getRecommendedTrips")
            .set({
                userid: user2._id
            })
            .expect(200);

        expect(res9.body).toBeTruthy;
        expect(res9.body.trips).toHaveLength(1);
        expect(res9.body.trips[0].riderTrips).toHaveLength(1);

        const res10 = await request.post("/trips/acceptTrip")
            .send({
               userID: user2._id,
               tripID: res9.body.trips[0].drivertrip._id,
               usertripID: res9.body.trips[0].riderTrips[0]._id
            })
            .expect(200);

        expect(res10.body.status).toBe("OK");
        expect(res10.body.message).toBe("user successfully added to trip");

        const taggedusertrip = await TripStore.findById(res9.body.trips[0].riderTrips[0]._id);
        const taggeddrivertrip = await TripStore.findById(res9.body.trips[0].drivertrip._id);

        expect(taggedusertrip.taggedUsers).toEqual(expect.arrayContaining(["demo2"]));
        expect(taggedusertrip.chatroomID).toEqual(taggeddrivertrip.chatroomID);
        expect(taggedusertrip.isFulfilled).toEqual(true);

        const res11 = await request.post("/chat/sendMessage")
            .send({
                userID: user2._id,
                roomID: taggedusertrip.chatroomID,
                message: "hello"
            })
            .expect(200);


        
        expect(res11.body.messages).toHaveLength(1);
        expect(res11.body.messages[0].message).toBe("hello");
        expect(res11.body.messages[0].username).toBe("demo2");
        expect(res11.body.users).toEqual(expect.arrayContaining(["demo2", "demo1"]));

        const res12 = await request.get("/chat/getMessages")
            .set({
                userID: user._id,
                roomID: taggedusertrip.chatroomID
            })
            .expect(200);

        expect(res12.body).toEqual(res11.body);

        done();
    });


    it('RIDER AND DRIVER: registration + login + update profile + newTrip + DRIVER GET RECOMMENDEDTRIPS + DRIVER ACCEPTING TRIP + DRIVER CHAT + RIDER GET CHAT + RIDER GET TRIPS', async (done) => {
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
               "arrivalTime":"Nov 27, 2019 00:00:00",
               "departurePlace":"not assigned",
               "departureTime":"Nov 26, 2019 00:44:08",
               "isDriverTrip": false,
               "roomID":"not assigned",
               "taggedUsers":[ 
                  "not assigned"
               ],
               "tripID":"not assigned",
               "tripRoute":{ 
                  "nameValuePairs":{ 
                     "origin":"49.262141,-123.2500775",
                     "destination":"49.2614434,-123.25669599999998"
                  }
               },
               "userID": user._id,
               "username":"demo1"
            })
            .expect(200);

        expect(res4.body).toBeTruthy();
        expect(res4.body.username).toBe("demo1");
        expect(res4.body.tripRoute.status).toBe("OK");


        const demouser2 = {
            username: "demo2",
            email: "demo2@demo.com",
            password: "demodemodemo"
        }

        const res5 = await request.post("/users/register")
            .send(demouser2)
            .expect(200);

        const user2 = await User.findOne({email: demouser2.email});
        expect(user2.username).toBe(demouser2.username);
        expect(user2.email).toBe(demouser2.email);
        expect(user2.password).toBeTruthy();
        expect(res5.body).toBeTruthy();
        expect(res5.body.username).toBe(demouser2.username);
        expect(res5.body.email).toBe(demouser2.email);

        const res6 = await request.post("/users/login")
            .send({
                username: "demo2",
                password: "demodemodemo"
            })
            .expect(200);

        expect(res6.body).toBeTruthy();
        expect(res6.body.username).toBe("demo2");
        expect(res6.body.email).toBe("demo2@demo.com");



        const res7 = await request.put("/users/updateProfile")
            .send({
                userID: user2._id,
                interests: [0,0,0,0,0],
                firstName: "demo2",
                lastName: "demo2",
                age: 20,
                gender: "male",
                email: "demo2@demo.com",
                carCapacity: 4,
                fbToken: "a",
                isDriver: true
            })
            .expect(200);

        expect(res7.body._id).toEqual(user2._id.toString());
        expect(res7.body.firstName).toBe("demo2");
        expect(res7.body.lastName).toBe("demo2");
        expect(res7.body.age).toBe(20);
        expect(res7.body.gender).toBe("male");
        expect(res7.body.email).toBe("demo2@demo.com");
        expect(res7.body.carCapacity).toBe(4);
        expect(res7.body.fbToken).toBe("a");
        expect(res7.body.isDriver).toBe(true);

        const res8 = await request.post("/trips/newTrip")
            .send({ 
               "arrivalPlace":"not assigned",
               "arrivalTime":"Nov 27, 2019 00:00:00",
               "departurePlace":"not assigned",
               "departureTime":"Nov 26, 2019 00:44:08",
               "isDriverTrip": true,
               "roomID":"not assigned",
               "taggedUsers":[ 
                  "not assigned"
               ],
               "tripID":"not assigned",
               "tripRoute":{ 
                  "nameValuePairs":{ 
                     "origin":"49.262141,-123.2500775",
                     "destination":"49.2614434,-123.25669599999998"
                  }
               },
               "userID": user2._id,
               "username":"demo2"
            })
            .expect(200);

        expect(res8.body).toBeTruthy();
        expect(res8.body.username).toBe("demo2");
        expect(res8.body.tripRoute.status).toBe("OK");

        const res9 = await request.get("/trips/getRecommendedTrips")
            .set({
                userid: user2._id
            })
            .expect(200);

        expect(res9.body).toBeTruthy;
        expect(res9.body.trips).toHaveLength(1);
        expect(res9.body.trips[0].riderTrips).toHaveLength(1);

        const res10 = await request.post("/trips/acceptTrip")
            .send({
               userID: user2._id,
               tripID: res9.body.trips[0].drivertrip._id,
               usertripID: res9.body.trips[0].riderTrips[0]._id
            })
            .expect(200);

        expect(res10.body.status).toBe("OK");
        expect(res10.body.message).toBe("user successfully added to trip");

        const taggedusertrip = await TripStore.findById(res9.body.trips[0].riderTrips[0]._id);
        const taggeddrivertrip = await TripStore.findById(res9.body.trips[0].drivertrip._id);

        expect(taggedusertrip.taggedUsers).toEqual(expect.arrayContaining(["demo2"]));
        expect(taggedusertrip.chatroomID).toEqual(taggeddrivertrip.chatroomID);
        expect(taggedusertrip.isFulfilled).toEqual(true);

        const res11 = await request.post("/chat/sendMessage")
            .send({
                userID: user2._id,
                roomID: taggedusertrip.chatroomID,
                message: "hello"
            })
            .expect(200);


        
        expect(res11.body.messages).toHaveLength(1);
        expect(res11.body.messages[0].message).toBe("hello");
        expect(res11.body.messages[0].username).toBe("demo2");
        expect(res11.body.users).toEqual(expect.arrayContaining(["demo2", "demo1"]));

        const res12 = await request.get("/chat/getMessages")
            .set({
                userID: user._id,
                roomID: taggedusertrip.chatroomID
            })
            .expect(200);

        expect(res12.body).toEqual(res11.body);

        const res13 = await request.get("/trips/myTrips")
            .set({
                userid: user._id
            })
            .expect(200);


        expect(res13.body.trips).toHaveLength(1);
        expect(res13.body.trips[0].username).toBe("demo1");
        expect(res13.body.trips[0].isDriverTrip).toBe(false);
        expect(res13.body.trips[0].isFulfilled).toBe(true);

        done();
    });

    it('RIDER AND DRIVER: registration + login + update profile + newTrip + DRIVER GET RECOMMENDEDTRIPS + DRIVER ACCEPTING TRIP + DRIVER CHAT + RIDER GET CHAT + RIDER GET TRIPS + RIDER DELETES TRIP', async (done) => {
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
               "arrivalTime":"Nov 27, 2019 00:00:00",
               "departurePlace":"not assigned",
               "departureTime":"Nov 26, 2019 00:44:08",
               "isDriverTrip": false,
               "roomID":"not assigned",
               "taggedUsers":[ 
                  "not assigned"
               ],
               "tripID":"not assigned",
               "tripRoute":{ 
                  "nameValuePairs":{ 
                     "origin":"49.262141,-123.2500775",
                     "destination":"49.2614434,-123.25669599999998"
                  }
               },
               "userID": user._id,
               "username":"demo1"
            })
            .expect(200);

        expect(res4.body).toBeTruthy();
        expect(res4.body.username).toBe("demo1");
        expect(res4.body.tripRoute.status).toBe("OK");


        const demouser2 = {
            username: "demo2",
            email: "demo2@demo.com",
            password: "demodemodemo"
        }

        const res5 = await request.post("/users/register")
            .send(demouser2)
            .expect(200);

        const user2 = await User.findOne({email: demouser2.email});
        expect(user2.username).toBe(demouser2.username);
        expect(user2.email).toBe(demouser2.email);
        expect(user2.password).toBeTruthy();
        expect(res5.body).toBeTruthy();
        expect(res5.body.username).toBe(demouser2.username);
        expect(res5.body.email).toBe(demouser2.email);

        const res6 = await request.post("/users/login")
            .send({
                username: "demo2",
                password: "demodemodemo"
            })
            .expect(200);

        expect(res6.body).toBeTruthy();
        expect(res6.body.username).toBe("demo2");
        expect(res6.body.email).toBe("demo2@demo.com");



        const res7 = await request.put("/users/updateProfile")
            .send({
                userID: user2._id,
                interests: [0,0,0,0,0],
                firstName: "demo2",
                lastName: "demo2",
                age: 20,
                gender: "male",
                email: "demo2@demo.com",
                carCapacity: 4,
                fbToken: "a",
                isDriver: true
            })
            .expect(200);

        expect(res7.body._id).toEqual(user2._id.toString());
        expect(res7.body.firstName).toBe("demo2");
        expect(res7.body.lastName).toBe("demo2");
        expect(res7.body.age).toBe(20);
        expect(res7.body.gender).toBe("male");
        expect(res7.body.email).toBe("demo2@demo.com");
        expect(res7.body.carCapacity).toBe(4);
        expect(res7.body.fbToken).toBe("a");
        expect(res7.body.isDriver).toBe(true);

        const res8 = await request.post("/trips/newTrip")
            .send({ 
               "arrivalPlace":"not assigned",
               "arrivalTime":"Nov 27, 2019 00:00:00",
               "departurePlace":"not assigned",
               "departureTime":"Nov 26, 2019 00:44:08",
               "isDriverTrip": true,
               "roomID":"not assigned",
               "taggedUsers":[ 
                  "not assigned"
               ],
               "tripID":"not assigned",
               "tripRoute":{ 
                  "nameValuePairs":{ 
                     "origin":"49.262141,-123.2500775",
                     "destination":"49.2614434,-123.25669599999998"
                  }
               },
               "userID": user2._id,
               "username":"demo2"
            })
            .expect(200);

        expect(res8.body).toBeTruthy();
        expect(res8.body.username).toBe("demo2");
        expect(res8.body.tripRoute.status).toBe("OK");

        const res9 = await request.get("/trips/getRecommendedTrips")
            .set({
                userid: user2._id
            })
            .expect(200);

        expect(res9.body).toBeTruthy;
        expect(res9.body.trips).toHaveLength(1);
        expect(res9.body.trips[0].riderTrips).toHaveLength(1);

        const res10 = await request.post("/trips/acceptTrip")
            .send({
               userID: user2._id,
               tripID: res9.body.trips[0].drivertrip._id,
               usertripID: res9.body.trips[0].riderTrips[0]._id
            })
            .expect(200);

        expect(res10.body.status).toBe("OK");
        expect(res10.body.message).toBe("user successfully added to trip");

        const taggedusertrip = await TripStore.findById(res9.body.trips[0].riderTrips[0]._id);
        const taggeddrivertrip = await TripStore.findById(res9.body.trips[0].drivertrip._id);

        expect(taggedusertrip.taggedUsers).toEqual(expect.arrayContaining(["demo2"]));
        expect(taggedusertrip.chatroomID).toEqual(taggeddrivertrip.chatroomID);
        expect(taggedusertrip.isFulfilled).toEqual(true);

        const res11 = await request.post("/chat/sendMessage")
            .send({
                userID: user2._id,
                roomID: taggedusertrip.chatroomID,
                message: "hello"
            })
            .expect(200);


        
        expect(res11.body.messages).toHaveLength(1);
        expect(res11.body.messages[0].message).toBe("hello");
        expect(res11.body.messages[0].username).toBe("demo2");
        expect(res11.body.users).toEqual(expect.arrayContaining(["demo2", "demo1"]));

        const res12 = await request.get("/chat/getMessages")
            .set({
                userID: user._id,
                roomID: taggedusertrip.chatroomID
            })
            .expect(200);

        expect(res12.body).toEqual(res11.body);

        const res13 = await request.get("/trips/myTrips")
            .set({
                userid: user._id
            })
            .expect(200);


        expect(res13.body.trips).toHaveLength(1);
        expect(res13.body.trips[0].username).toBe("demo1");
        expect(res13.body.trips[0].isDriverTrip).toBe(false);
        expect(res13.body.trips[0].isFulfilled).toBe(true);

        const res14 = await request.del("/trips/delTrip")
            .set({
                userid: user._id,
                tripid: res9.body.trips[0].riderTrips[0]._id
            })
            .expect(200);

        expect(res14.body.status).toBe("OK");
        expect(res14.body.message).toBe("trip successfully deleted");

        const afterRiderDeletedDriverTrip = await TripStore.findById(res9.body.trips[0].drivertrip._id);

        expect(afterRiderDeletedDriverTrip.taggedUsers).toHaveLength(0);

        done();
    });

    it('RIDER AND DRIVER: registration + login + update profile + newTrip + DRIVER GET RECOMMENDEDTRIPS + DRIVER ACCEPTING TRIP + DRIVER CHAT + RIDER GET CHAT + RIDER GET TRIPS + RIDER DELETES TRIP + Driver get Users Profile', async (done) => {
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
               "arrivalTime":"Nov 27, 2019 00:00:00",
               "departurePlace":"not assigned",
               "departureTime":"Nov 26, 2019 00:44:08",
               "isDriverTrip": false,
               "roomID":"not assigned",
               "taggedUsers":[ 
                  "not assigned"
               ],
               "tripID":"not assigned",
               "tripRoute":{ 
                  "nameValuePairs":{ 
                     "origin":"49.262141,-123.2500775",
                     "destination":"49.2614434,-123.25669599999998"
                  }
               },
               "userID": user._id,
               "username":"demo1"
            })
            .expect(200);

        expect(res4.body).toBeTruthy();
        expect(res4.body.username).toBe("demo1");
        expect(res4.body.tripRoute.status).toBe("OK");


        const demouser2 = {
            username: "demo2",
            email: "demo2@demo.com",
            password: "demodemodemo"
        }

        const res5 = await request.post("/users/register")
            .send(demouser2)
            .expect(200);

        const user2 = await User.findOne({email: demouser2.email});
        expect(user2.username).toBe(demouser2.username);
        expect(user2.email).toBe(demouser2.email);
        expect(user2.password).toBeTruthy();
        expect(res5.body).toBeTruthy();
        expect(res5.body.username).toBe(demouser2.username);
        expect(res5.body.email).toBe(demouser2.email);

        const res6 = await request.post("/users/login")
            .send({
                username: "demo2",
                password: "demodemodemo"
            })
            .expect(200);

        expect(res6.body).toBeTruthy();
        expect(res6.body.username).toBe("demo2");
        expect(res6.body.email).toBe("demo2@demo.com");



        const res7 = await request.put("/users/updateProfile")
            .send({
                userID: user2._id,
                interests: [0,0,0,0,0],
                firstName: "demo2",
                lastName: "demo2",
                age: 20,
                gender: "male",
                email: "demo2@demo.com",
                carCapacity: 4,
                fbToken: "a",
                isDriver: true
            })
            .expect(200);

        expect(res7.body._id).toEqual(user2._id.toString());
        expect(res7.body.firstName).toBe("demo2");
        expect(res7.body.lastName).toBe("demo2");
        expect(res7.body.age).toBe(20);
        expect(res7.body.gender).toBe("male");
        expect(res7.body.email).toBe("demo2@demo.com");
        expect(res7.body.carCapacity).toBe(4);
        expect(res7.body.fbToken).toBe("a");
        expect(res7.body.isDriver).toBe(true);

        const res8 = await request.post("/trips/newTrip")
            .send({ 
               "arrivalPlace":"not assigned",
               "arrivalTime":"Nov 27, 2019 00:00:00",
               "departurePlace":"not assigned",
               "departureTime":"Nov 26, 2019 00:44:08",
               "isDriverTrip": true,
               "roomID":"not assigned",
               "taggedUsers":[ 
                  "not assigned"
               ],
               "tripID":"not assigned",
               "tripRoute":{ 
                  "nameValuePairs":{ 
                     "origin":"49.262141,-123.2500775",
                     "destination":"49.2614434,-123.25669599999998"
                  }
               },
               "userID": user2._id,
               "username":"demo2"
            })
            .expect(200);

        expect(res8.body).toBeTruthy();
        expect(res8.body.username).toBe("demo2");
        expect(res8.body.tripRoute.status).toBe("OK");

        const res9 = await request.get("/trips/getRecommendedTrips")
            .set({
                userid: user2._id
            })
            .expect(200);

        expect(res9.body).toBeTruthy;
        expect(res9.body.trips).toHaveLength(1);
        expect(res9.body.trips[0].riderTrips).toHaveLength(1);

        const res10 = await request.post("/trips/acceptTrip")
            .send({
               userID: user2._id,
               tripID: res9.body.trips[0].drivertrip._id,
               usertripID: res9.body.trips[0].riderTrips[0]._id
            })
            .expect(200);

        expect(res10.body.status).toBe("OK");
        expect(res10.body.message).toBe("user successfully added to trip");

        const taggedusertrip = await TripStore.findById(res9.body.trips[0].riderTrips[0]._id);
        const taggeddrivertrip = await TripStore.findById(res9.body.trips[0].drivertrip._id);

        expect(taggedusertrip.taggedUsers).toEqual(expect.arrayContaining(["demo2"]));
        expect(taggedusertrip.chatroomID).toEqual(taggeddrivertrip.chatroomID);
        expect(taggedusertrip.isFulfilled).toEqual(true);

        const res11 = await request.post("/chat/sendMessage")
            .send({
                userID: user2._id,
                roomID: taggedusertrip.chatroomID,
                message: "hello"
            })
            .expect(200);


        
        expect(res11.body.messages).toHaveLength(1);
        expect(res11.body.messages[0].message).toBe("hello");
        expect(res11.body.messages[0].username).toBe("demo2");
        expect(res11.body.users).toEqual(expect.arrayContaining(["demo2", "demo1"]));

        const res12 = await request.get("/chat/getMessages")
            .set({
                userID: user._id,
                roomID: taggedusertrip.chatroomID
            })
            .expect(200);

        expect(res12.body).toEqual(res11.body);

        const res13 = await request.get("/trips/myTrips")
            .set({
                userid: user._id
            })
            .expect(200);


        expect(res13.body.trips).toHaveLength(1);
        expect(res13.body.trips[0].username).toBe("demo1");
        expect(res13.body.trips[0].isDriverTrip).toBe(false);
        expect(res13.body.trips[0].isFulfilled).toBe(true);

        const res14 = await request.del("/trips/delTrip")
            .set({
                userid: user._id,
                tripid: res9.body.trips[0].riderTrips[0]._id
            })
            .expect(200);

        expect(res14.body.status).toBe("OK");
        expect(res14.body.message).toBe("trip successfully deleted");

        const afterRiderDeletedDriverTrip = await TripStore.findById(res9.body.trips[0].drivertrip._id);

        expect(afterRiderDeletedDriverTrip.taggedUsers).toHaveLength(0);

        const res15 = await request.get("/users/getProfile")
            .set({
               username: "demo1" 
            })
            .expect(200);

        expect(res15.body.username).toBe("demo1");
        expect(res15.body.email).toBe("demo@demo.com");
        expect(res15.body.isDriver).toBe(false);

        done();
    });

    it('RIDER AND DRIVER: registration + login + update profile + newTrip + DRIVER GET RECOMMENDEDTRIPS + DRIVER ACCEPTING TRIP + DRIVER CHAT + RIDER GET CHAT + RIDER GET TRIPS + RIDER DELETES TRIP + Driver get Users Profile', async (done) => {
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
               "arrivalTime":"Nov 27, 2019 00:00:00",
               "departurePlace":"not assigned",
               "departureTime":"Nov 26, 2019 00:44:08",
               "isDriverTrip": false,
               "roomID":"not assigned",
               "taggedUsers":[ 
                  "not assigned"
               ],
               "tripID":"not assigned",
               "tripRoute":{ 
                  "nameValuePairs":{ 
                     "origin":"49.262141,-123.2500775",
                     "destination":"49.2614434,-123.25669599999998"
                  }
               },
               "userID": user._id,
               "username":"demo1"
            })
            .expect(200);

        expect(res4.body).toBeTruthy();
        expect(res4.body.username).toBe("demo1");
        expect(res4.body.tripRoute.status).toBe("OK");


        const demouser2 = {
            username: "demo2",
            email: "demo2@demo.com",
            password: "demodemodemo"
        }

        const res5 = await request.post("/users/register")
            .send(demouser2)
            .expect(200);

        const user2 = await User.findOne({email: demouser2.email});
        expect(user2.username).toBe(demouser2.username);
        expect(user2.email).toBe(demouser2.email);
        expect(user2.password).toBeTruthy();
        expect(res5.body).toBeTruthy();
        expect(res5.body.username).toBe(demouser2.username);
        expect(res5.body.email).toBe(demouser2.email);

        const res6 = await request.post("/users/login")
            .send({
                username: "demo2",
                password: "demodemodemo"
            })
            .expect(200);

        expect(res6.body).toBeTruthy();
        expect(res6.body.username).toBe("demo2");
        expect(res6.body.email).toBe("demo2@demo.com");



        const res7 = await request.put("/users/updateProfile")
            .send({
                userID: user2._id,
                interests: [0,0,0,0,0],
                firstName: "demo2",
                lastName: "demo2",
                age: 20,
                gender: "male",
                email: "demo2@demo.com",
                carCapacity: 4,
                fbToken: "a",
                isDriver: true
            })
            .expect(200);

        expect(res7.body._id).toEqual(user2._id.toString());
        expect(res7.body.firstName).toBe("demo2");
        expect(res7.body.lastName).toBe("demo2");
        expect(res7.body.age).toBe(20);
        expect(res7.body.gender).toBe("male");
        expect(res7.body.email).toBe("demo2@demo.com");
        expect(res7.body.carCapacity).toBe(4);
        expect(res7.body.fbToken).toBe("a");
        expect(res7.body.isDriver).toBe(true);

        const res8 = await request.post("/trips/newTrip")
            .send({ 
               "arrivalPlace":"not assigned",
               "arrivalTime":"Nov 27, 2019 00:00:00",
               "departurePlace":"not assigned",
               "departureTime":"Nov 26, 2019 00:44:08",
               "isDriverTrip": true,
               "roomID":"not assigned",
               "taggedUsers":[ 
                  "not assigned"
               ],
               "tripID":"not assigned",
               "tripRoute":{ 
                  "nameValuePairs":{ 
                     "origin":"49.262141,-123.2500775",
                     "destination":"49.2614434,-123.25669599999998"
                  }
               },
               "userID": user2._id,
               "username":"demo2"
            })
            .expect(200);

        expect(res8.body).toBeTruthy();
        expect(res8.body.username).toBe("demo2");
        expect(res8.body.tripRoute.status).toBe("OK");

        const res9 = await request.get("/trips/getRecommendedTrips")
            .set({
                userid: user2._id
            })
            .expect(200);

        expect(res9.body).toBeTruthy;
        expect(res9.body.trips).toHaveLength(1);
        expect(res9.body.trips[0].riderTrips).toHaveLength(1);

        const res10 = await request.post("/trips/acceptTrip")
            .send({
               userID: user2._id,
               tripID: res9.body.trips[0].drivertrip._id,
               usertripID: res9.body.trips[0].riderTrips[0]._id
            })
            .expect(200);

        expect(res10.body.status).toBe("OK");
        expect(res10.body.message).toBe("user successfully added to trip");

        const taggedusertrip = await TripStore.findById(res9.body.trips[0].riderTrips[0]._id);
        const taggeddrivertrip = await TripStore.findById(res9.body.trips[0].drivertrip._id);

        expect(taggedusertrip.taggedUsers).toEqual(expect.arrayContaining(["demo2"]));
        expect(taggedusertrip.chatroomID).toEqual(taggeddrivertrip.chatroomID);
        expect(taggedusertrip.isFulfilled).toEqual(true);

        const res11 = await request.post("/chat/sendMessage")
            .send({
                userID: user2._id,
                roomID: taggedusertrip.chatroomID,
                message: "hello"
            })
            .expect(200);


        
        expect(res11.body.messages).toHaveLength(1);
        expect(res11.body.messages[0].message).toBe("hello");
        expect(res11.body.messages[0].username).toBe("demo2");
        expect(res11.body.users).toEqual(expect.arrayContaining(["demo2", "demo1"]));

        const res12 = await request.get("/chat/getMessages")
            .set({
                userID: user._id,
                roomID: taggedusertrip.chatroomID
            })
            .expect(200);

        expect(res12.body).toEqual(res11.body);

        const res13 = await request.get("/trips/myTrips")
            .set({
                userid: user._id
            })
            .expect(200);


        expect(res13.body.trips).toHaveLength(1);
        expect(res13.body.trips[0].username).toBe("demo1");
        expect(res13.body.trips[0].isDriverTrip).toBe(false);
        expect(res13.body.trips[0].isFulfilled).toBe(true);

        const res14 = await request.del("/trips/delTrip")
            .set({
                userid: user._id,
                tripid: res9.body.trips[0].riderTrips[0]._id
            })
            .expect(200);

        expect(res14.body.status).toBe("OK");
        expect(res14.body.message).toBe("trip successfully deleted");

        const afterRiderDeletedDriverTrip = await TripStore.findById(res9.body.trips[0].drivertrip._id);

        expect(afterRiderDeletedDriverTrip.taggedUsers).toHaveLength(0);

        const res15 = await request.get("/users/getProfile")
            .set({
               username: "demo1" 
            })
            .expect(200);

        expect(res15.body.username).toBe("demo1");
        expect(res15.body.email).toBe("demo@demo.com");
        expect(res15.body.isDriver).toBe(false);

        done();
    });

    it('RIDER AND DRIVER: registration + login + update profile + newTrip + DRIVER GET RECOMMENDEDTRIPS + DRIVER ACCEPTING TRIP + DRIVER CHAT + RIDER GET CHAT + RIDER GET TRIPS + RIDER DELETES TRIP + Driver get Users Profile + Driver DELETE TRIP', async (done) => {
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
               "arrivalTime":"Nov 27, 2019 00:00:00",
               "departurePlace":"not assigned",
               "departureTime":"Nov 26, 2019 00:44:08",
               "isDriverTrip": false,
               "roomID":"not assigned",
               "taggedUsers":[ 
                  "not assigned"
               ],
               "tripID":"not assigned",
               "tripRoute":{ 
                  "nameValuePairs":{ 
                     "origin":"49.262141,-123.2500775",
                     "destination":"49.2614434,-123.25669599999998"
                  }
               },
               "userID": user._id,
               "username":"demo1"
            })
            .expect(200);

        expect(res4.body).toBeTruthy();
        expect(res4.body.username).toBe("demo1");
        expect(res4.body.tripRoute.status).toBe("OK");


        const demouser2 = {
            username: "demo2",
            email: "demo2@demo.com",
            password: "demodemodemo"
        }

        const res5 = await request.post("/users/register")
            .send(demouser2)
            .expect(200);

        const user2 = await User.findOne({email: demouser2.email});
        expect(user2.username).toBe(demouser2.username);
        expect(user2.email).toBe(demouser2.email);
        expect(user2.password).toBeTruthy();
        expect(res5.body).toBeTruthy();
        expect(res5.body.username).toBe(demouser2.username);
        expect(res5.body.email).toBe(demouser2.email);

        const res6 = await request.post("/users/login")
            .send({
                username: "demo2",
                password: "demodemodemo"
            })
            .expect(200);

        expect(res6.body).toBeTruthy();
        expect(res6.body.username).toBe("demo2");
        expect(res6.body.email).toBe("demo2@demo.com");



        const res7 = await request.put("/users/updateProfile")
            .send({
                userID: user2._id,
                interests: [0,0,0,0,0],
                firstName: "demo2",
                lastName: "demo2",
                age: 20,
                gender: "male",
                email: "demo2@demo.com",
                carCapacity: 4,
                fbToken: "a",
                isDriver: true
            })
            .expect(200);

        expect(res7.body._id).toEqual(user2._id.toString());
        expect(res7.body.firstName).toBe("demo2");
        expect(res7.body.lastName).toBe("demo2");
        expect(res7.body.age).toBe(20);
        expect(res7.body.gender).toBe("male");
        expect(res7.body.email).toBe("demo2@demo.com");
        expect(res7.body.carCapacity).toBe(4);
        expect(res7.body.fbToken).toBe("a");
        expect(res7.body.isDriver).toBe(true);

        const res8 = await request.post("/trips/newTrip")
            .send({ 
               "arrivalPlace":"not assigned",
               "arrivalTime":"Nov 27, 2019 00:00:00",
               "departurePlace":"not assigned",
               "departureTime":"Nov 26, 2019 00:44:08",
               "isDriverTrip": true,
               "roomID":"not assigned",
               "taggedUsers":[ 
                  "not assigned"
               ],
               "tripID":"not assigned",
               "tripRoute":{ 
                  "nameValuePairs":{ 
                     "origin":"49.262141,-123.2500775",
                     "destination":"49.2614434,-123.25669599999998"
                  }
               },
               "userID": user2._id,
               "username":"demo2"
            })
            .expect(200);

        expect(res8.body).toBeTruthy();
        expect(res8.body.username).toBe("demo2");
        expect(res8.body.tripRoute.status).toBe("OK");

        const res9 = await request.get("/trips/getRecommendedTrips")
            .set({
                userid: user2._id
            })
            .expect(200);

        expect(res9.body).toBeTruthy;
        expect(res9.body.trips).toHaveLength(1);
        expect(res9.body.trips[0].riderTrips).toHaveLength(1);

        const res10 = await request.post("/trips/acceptTrip")
            .send({
               userID: user2._id,
               tripID: res9.body.trips[0].drivertrip._id,
               usertripID: res9.body.trips[0].riderTrips[0]._id
            })
            .expect(200);

        expect(res10.body.status).toBe("OK");
        expect(res10.body.message).toBe("user successfully added to trip");

        const taggedusertrip = await TripStore.findById(res9.body.trips[0].riderTrips[0]._id);
        const taggeddrivertrip = await TripStore.findById(res9.body.trips[0].drivertrip._id);

        expect(taggedusertrip.taggedUsers).toEqual(expect.arrayContaining(["demo2"]));
        expect(taggedusertrip.chatroomID).toEqual(taggeddrivertrip.chatroomID);
        expect(taggedusertrip.isFulfilled).toEqual(true);

        const res11 = await request.post("/chat/sendMessage")
            .send({
                userID: user2._id,
                roomID: taggedusertrip.chatroomID,
                message: "hello"
            })
            .expect(200);


        
        expect(res11.body.messages).toHaveLength(1);
        expect(res11.body.messages[0].message).toBe("hello");
        expect(res11.body.messages[0].username).toBe("demo2");
        expect(res11.body.users).toEqual(expect.arrayContaining(["demo2", "demo1"]));

        const res12 = await request.get("/chat/getMessages")
            .set({
                userID: user._id,
                roomID: taggedusertrip.chatroomID
            })
            .expect(200);

        expect(res12.body).toEqual(res11.body);

        const res13 = await request.get("/trips/myTrips")
            .set({
                userid: user._id
            })
            .expect(200);


        expect(res13.body.trips).toHaveLength(1);
        expect(res13.body.trips[0].username).toBe("demo1");
        expect(res13.body.trips[0].isDriverTrip).toBe(false);
        expect(res13.body.trips[0].isFulfilled).toBe(true);

        const res14 = await request.del("/trips/delTrip")
            .set({
                userid: user._id,
                tripid: res9.body.trips[0].riderTrips[0]._id
            })
            .expect(200);

        expect(res14.body.status).toBe("OK");
        expect(res14.body.message).toBe("trip successfully deleted");

        const afterRiderDeletedDriverTrip = await TripStore.findById(res9.body.trips[0].drivertrip._id);

        expect(afterRiderDeletedDriverTrip.taggedUsers).toHaveLength(0);

        const res15 = await request.get("/users/getProfile")
            .set({
               username: "demo1" 
            })
            .expect(200);

        expect(res15.body.username).toBe("demo1");
        expect(res15.body.email).toBe("demo@demo.com");
        expect(res15.body.isDriver).toBe(false);

        const res16 = await request.del("/trips/delTrip")
            .set({
                userid: user2._id,
                tripid: res9.body.trips[0].drivertrip._id
            })
            .expect(200);

        expect(res16.body.status).toBe("OK");
        expect(res16.body.message).toBe("trip successfully deleted");

        const afterDriverDeletedDriverTrip = await TripStore.findById(res9.body.trips[0].drivertrip._id);

        expect(afterDriverDeletedDriverTrip).toBeNull();

        done();

    });

    it('RIDER AND DRIVER: registration + login + update profile + newTrip + DRIVER GET RECOMMENDEDTRIPS + DRIVER ACCEPTING TRIP + DRIVER CHAT + RIDER GET CHAT + RIDER GET TRIPS + RIDER DELETES TRIP + Driver get Users Profile + Driver DELETE TRIP + DRIVER LOG OUT', async (done) => {
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
               "arrivalTime":"Nov 27, 2019 00:00:00",
               "departurePlace":"not assigned",
               "departureTime":"Nov 26, 2019 00:44:08",
               "isDriverTrip": false,
               "roomID":"not assigned",
               "taggedUsers":[ 
                  "not assigned"
               ],
               "tripID":"not assigned",
               "tripRoute":{ 
                  "nameValuePairs":{ 
                     "origin":"49.262141,-123.2500775",
                     "destination":"49.2614434,-123.25669599999998"
                  }
               },
               "userID": user._id,
               "username":"demo1"
            })
            .expect(200);

        expect(res4.body).toBeTruthy();
        expect(res4.body.username).toBe("demo1");
        expect(res4.body.tripRoute.status).toBe("OK");


        const demouser2 = {
            username: "demo2",
            email: "demo2@demo.com",
            password: "demodemodemo"
        }

        const res5 = await request.post("/users/register")
            .send(demouser2)
            .expect(200);

        const user2 = await User.findOne({email: demouser2.email});
        expect(user2.username).toBe(demouser2.username);
        expect(user2.email).toBe(demouser2.email);
        expect(user2.password).toBeTruthy();
        expect(res5.body).toBeTruthy();
        expect(res5.body.username).toBe(demouser2.username);
        expect(res5.body.email).toBe(demouser2.email);

        const res6 = await request.post("/users/login")
            .send({
                username: "demo2",
                password: "demodemodemo"
            })
            .expect(200);

        expect(res6.body).toBeTruthy();
        expect(res6.body.username).toBe("demo2");
        expect(res6.body.email).toBe("demo2@demo.com");



        const res7 = await request.put("/users/updateProfile")
            .send({
                userID: user2._id,
                interests: [0,0,0,0,0],
                firstName: "demo2",
                lastName: "demo2",
                age: 20,
                gender: "male",
                email: "demo2@demo.com",
                carCapacity: 4,
                fbToken: "a",
                isDriver: true
            })
            .expect(200);

        expect(res7.body._id).toEqual(user2._id.toString());
        expect(res7.body.firstName).toBe("demo2");
        expect(res7.body.lastName).toBe("demo2");
        expect(res7.body.age).toBe(20);
        expect(res7.body.gender).toBe("male");
        expect(res7.body.email).toBe("demo2@demo.com");
        expect(res7.body.carCapacity).toBe(4);
        expect(res7.body.fbToken).toBe("a");
        expect(res7.body.isDriver).toBe(true);

        const res8 = await request.post("/trips/newTrip")
            .send({ 
               "arrivalPlace":"not assigned",
               "arrivalTime":"Nov 27, 2019 00:00:00",
               "departurePlace":"not assigned",
               "departureTime":"Nov 26, 2019 00:44:08",
               "isDriverTrip": true,
               "roomID":"not assigned",
               "taggedUsers":[ 
                  "not assigned"
               ],
               "tripID":"not assigned",
               "tripRoute":{ 
                  "nameValuePairs":{ 
                     "origin":"49.262141,-123.2500775",
                     "destination":"49.2614434,-123.25669599999998"
                  }
               },
               "userID": user2._id,
               "username":"demo2"
            })
            .expect(200);

        expect(res8.body).toBeTruthy();
        expect(res8.body.username).toBe("demo2");
        expect(res8.body.tripRoute.status).toBe("OK");

        const res9 = await request.get("/trips/getRecommendedTrips")
            .set({
                userid: user2._id
            })
            .expect(200);

        expect(res9.body).toBeTruthy;
        expect(res9.body.trips).toHaveLength(1);
        expect(res9.body.trips[0].riderTrips).toHaveLength(1);

        const res10 = await request.post("/trips/acceptTrip")
            .send({
               userID: user2._id,
               tripID: res9.body.trips[0].drivertrip._id,
               usertripID: res9.body.trips[0].riderTrips[0]._id
            })
            .expect(200);

        expect(res10.body.status).toBe("OK");
        expect(res10.body.message).toBe("user successfully added to trip");

        const taggedusertrip = await TripStore.findById(res9.body.trips[0].riderTrips[0]._id);
        const taggeddrivertrip = await TripStore.findById(res9.body.trips[0].drivertrip._id);

        expect(taggedusertrip.taggedUsers).toEqual(expect.arrayContaining(["demo2"]));
        expect(taggedusertrip.chatroomID).toEqual(taggeddrivertrip.chatroomID);
        expect(taggedusertrip.isFulfilled).toEqual(true);

        const res11 = await request.post("/chat/sendMessage")
            .send({
                userID: user2._id,
                roomID: taggedusertrip.chatroomID,
                message: "hello"
            })
            .expect(200);


        
        expect(res11.body.messages).toHaveLength(1);
        expect(res11.body.messages[0].message).toBe("hello");
        expect(res11.body.messages[0].username).toBe("demo2");
        expect(res11.body.users).toEqual(expect.arrayContaining(["demo2", "demo1"]));

        const res12 = await request.get("/chat/getMessages")
            .set({
                userID: user._id,
                roomID: taggedusertrip.chatroomID
            })
            .expect(200);

        expect(res12.body).toEqual(res11.body);

        const res13 = await request.get("/trips/myTrips")
            .set({
                userid: user._id
            })
            .expect(200);


        expect(res13.body.trips).toHaveLength(1);
        expect(res13.body.trips[0].username).toBe("demo1");
        expect(res13.body.trips[0].isDriverTrip).toBe(false);
        expect(res13.body.trips[0].isFulfilled).toBe(true);

        const res14 = await request.del("/trips/delTrip")
            .set({
                userid: user._id,
                tripid: res9.body.trips[0].riderTrips[0]._id
            })
            .expect(200);

        expect(res14.body.status).toBe("OK");
        expect(res14.body.message).toBe("trip successfully deleted");

        const afterRiderDeletedDriverTrip = await TripStore.findById(res9.body.trips[0].drivertrip._id);

        expect(afterRiderDeletedDriverTrip.taggedUsers).toHaveLength(0);

        const res15 = await request.get("/users/getProfile")
            .set({
               username: "demo1" 
            })
            .expect(200);

        expect(res15.body.username).toBe("demo1");
        expect(res15.body.email).toBe("demo@demo.com");
        expect(res15.body.isDriver).toBe(false);

        const res16 = await request.del("/trips/delTrip")
            .set({
                userid: user2._id,
                tripid: res9.body.trips[0].drivertrip._id
            })
            .expect(200);

        expect(res16.body.status).toBe("OK");
        expect(res16.body.message).toBe("trip successfully deleted");

        const afterDriverDeletedDriverTrip = await TripStore.findById(res9.body.trips[0].drivertrip._id);

        expect(afterDriverDeletedDriverTrip).toBeNull();


        const res17 = await request.post("/users/logout")
            .send({
                userID: user2._id
            })
            .expect(200);

        const loggedoutUser = await User.findById(user2._id);

        expect(loggedoutUser.fbToken).toBe("");

        done();

    });
})

