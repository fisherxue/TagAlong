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


const databaseName = 'triproutestest';

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

describe('testing trips', () => {
    it('should create a new rider trip', async (done) => {
        user1 = new User({
            username: "chatdemouser13",
            email: "chatdemouser13@demo.com",
            password: "demodemodemo",
            fbToken: ""
        });

        let newuser;

        await user1.save()
            .then((user) => {
                newuser = user;
            });

        const res = await request.post("/trips/newTrip")
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
               "userID": user1._id,
               "username": user1.username
            })
            .expect(200);


        expect(res.body).toBeTruthy();
        expect(res.body.isFulfilled).toBe(false);
        expect(res.body.tripRoute).toBeTruthy();
        expect(res.body.tripRoute.status).toBe("OK");
        expect(res.body.isDriverTrip).toBe(false);


        done();
    })

    it('should create a new driver trip', async (done) => {
        user1 = new User({
            username: "chatdemouser14",
            email: "chatdemouser14@demo.com",
            password: "demodemodemo",
            fbToken: ""
        });

        let newuser;

        await user1.save()
            .then((user) => {
                newuser = user;
            });

        const res = await request.post("/trips/newTrip")
            .send({ 
               "arrivalPlace":"not assigned",
               "arrivalTime":"Nov 30, 2019 06:00:00",
               "departurePlace":"not assigned",
               "departureTime":"Nov 18, 2019 23:58:44",
               "isDriverTrip":true,
               "roomID":"not assigned",
               "taggedUsers":[ 
                  "not assigned"
               ],
               "tripID":"not assigned",
               "tripRoute":{ 
                  "nameValuePairs":{ 
                     "origin":"49.2621715,-123.250049",
                     "destination":"49.33266018909084,-123.08990802615881"
                  }
               },
               "userID": newuser._id,
               "username": newuser.username
            })
            .expect(200);

        expect(res.body).toBeTruthy();
        expect(res.body.isFulfilled).toBe(false);
        expect(res.body.tripRoute).toBeTruthy();
        expect(res.body.tripRoute.status).toBe("OK");
        expect(res.body.isDriverTrip).toBe(true);

        done();
    })

    it('should fail to create new drivertrip with invalid userID', async (done) => {

        const res = await request.post("/trips/newTrip")
            .send({ 
               "arrivalPlace":"not assigned",
               "arrivalTime":"Nov 30, 2019 06:00:00",
               "departurePlace":"not assigned",
               "departureTime":"Nov 18, 2019 23:58:44",
               "isDriverTrip":true,
               "roomID":"not assigned",
               "taggedUsers":[ 
                  "not assigned"
               ],
               "tripID":"not assigned",
               "tripRoute":{ 
                  "nameValuePairs":{ 
                     "origin":"49.2621715,-123.250049",
                     "destination":"49.33266018909084,-123.08990802615881"
                  }
               },
               "userID": "",
               "username": ""
            })
            .expect(400);

        expect(res.text).toBe("Invalid userID");

        done();
    })

    it('should fail to create new drivertrip with valid userID but unknown user', async (done) => {

        const res = await request.post("/trips/newTrip")
            .send({ 
               "arrivalPlace":"not assigned",
               "arrivalTime":"Nov 30, 2019 06:00:00",
               "departurePlace":"not assigned",
               "departureTime":"Nov 18, 2019 23:58:44",
               "isDriverTrip":true,
               "roomID":"not assigned",
               "taggedUsers":[ 
                  "not assigned"
               ],
               "tripID":"not assigned",
               "tripRoute":{ 
                  "nameValuePairs":{ 
                     "origin":"49.2621715,-123.250049",
                     "destination":"49.33266018909084,-123.08990802615881"
                  }
               },
               "userID": "5dd36a75b458db53e031818e",
               "username": ""
            })
            .expect(400);

        expect(res.text).toBe("Unable to find user");

        done();
    })

    it('new driver trip should match with 3 other user trips', async (done) => {
        
        user1 = new User({
            _id: "5dd36a75b458db53e031818e",
            username: "bwong",
            email: "bwong@demo.com",
            password: "demodemodemo",
            fbToken: ""
        });

        await user1.save();

        user2 = new User({
            _id: "5dd36c81b458db53e031819b",
            username: "bwong5",
            email: "bwong5@demo.com",
            password: "demodemodemo",
            fbToken: ""
        });

        await user2.save();

        user3 = new User({
            _id: "5dd36b0fb458db53e0318192",
            username: "bwong2",
            email: "bwong2@demo.com",
            password: "demodemodemo",
            fbToken: ""
        });

        await user3.save();

        let driver;

        user4 = new User({
            username: "bwong1",
            email: "bwong1@demo.com",
            password: "demodemodemo",
            fbToken: ""
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

        const res = await request.post("/trips/newTrip")
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


        done();

    })

})
