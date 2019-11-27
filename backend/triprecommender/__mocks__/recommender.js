const fs = require("fs");
const fsp = fs.promises;

const recommender = jest.genMockFromModule("../recommender.js");

async function addMockTrips() {
    let driverTrip;
    let riderTrips = [];
    let trip;
    trip = await fsp.readFile("./__test__/trips/driverTrip1.json", "utf8");
    trip = JSON.parse(trip);
    driverTrip = trip;
    trip = await fsp.readFile("./__test__/trips/riderTrip1.json", "utf8");
    trip = JSON.parse(trip);
    riderTrips.push(trip);
    trip = await fsp.readFile("./__test__/trips/riderTrip2.json", "utf8");
    trip = JSON.parse(trip);
    riderTrips.push(trip);
    trip = await fsp.readFile("./__test__/trips/riderTrip3.json", "utf8");
    trip = JSON.parse(trip);
    riderTrips.push(trip);
    return riderTrips;
}

async function driverTripHandler(driverTrip) {
    riderTrips = await addMockTrips()
    return riderTrips;

}

function tripHandler(trip, callback) {
    fs.readFile("./triprecommender/__mocks__/getDirections.json", "utf8", (err, data) => {
        callback(JSON.parse(data).json);
    });
}

async function modifyTrip(driverTrip, riderTrips, callback) {
    fs.readFile("./triprecommender/__mocks__/getDirections.json", "utf8", (err, data) => {
        callback(JSON.parse(data).json);
    });
}

recommender.driverTripHandler = driverTripHandler;
recommender.tripHandler = tripHandler;
recommender.modifyTrip = modifyTrip;

module.exports = recommender;