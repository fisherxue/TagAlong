
const LatLng = require('./latlng.js');

// database
const TripStore = require('../Trip/models/Trip');
const UserStore = require('../User/models/user');

/* Google Maps */
// initialize maps client
const googleMapsClient = require('@google/maps').createClient({
    key: 'AIzaSyDkjse1zwmX7lw71D5wpKIP0xrbKLG1YIQ'
});


const MaxDriverBearingDiff = 45; // 20 deg
const MaxDriverDistanceDiff = 2000; // 1km
const NumInterests = 10;

/* Get directions */
function getDirectionsWithWaypoints(req, callback) {
    googleMapsClient.directions({
        origin: req.origin,
        destination: req.destination,
        waypoints: req.waypoints,
    }, function(err, response) {
        if (err) throw err;

        callback(response);
    });
};
function getDirections(req, callback) {
    console.log(req, "921")
    googleMapsClient.directions({
        origin: req.origin,
        destination: req.destination,
    }, function(err, response) {
        if (err) console.log(err);
        callback(response);
    });
};

function driverTripHandler(driverTrip) {
    console.log("83")
    let riderTrips = getRiderTrips(driverTrip);
    console.log("93")
    riderTrips = getRiderTripSimilarity(driverTrip, riderTrips);
    console.log("103")
    return riderTrips;
}

function tripHandler(trip, callback) {
    let startPoint = trip.origin;
    let endPoint = trip.destination
    console.log(trip, "10")
    let req = {
        origin: startPoint,
        destination: endPoint
    }
    console.log(req, "88")
    getDirections(req, function(res) {
        console.log(res, "1")
        callback(res);
    });
}

/*
 * Modify the driver trip by adding waypoints for each 
 * rider trip start and end
 */ 
function modifyTrip(driverTrip, riderTrips, callback) {
    let waypoints = [];

    for (let i = 0; i < riderTrips.length; i++) {
        let startPoint = riderTrips[i].startLat + ',' + riderTrips[i].startLng;
        let endPoint = riderTrips[i].endLat + ',' + riderTrips[i].endLng
        waypoints.push(startPoint);
        waypoints.push(endPoint);
    }

    let driverStartPoint = driverTrip.startLat + ',' + driverTrip.startLng;
    let driverEndPoint = driverTrip.endLat + ',' + driverTrip.endLng;
    let req = {
        origin: driverStartPoint,
        destination: driverEndPoint,
        waypoints: waypoints
    }

    getDirectionsWithWaypoints(req, function(res) {
        callback(res);
    })
}

/*
 * Adds a field to riderTrip indicating similarity to given driver
 *
 * 
 */
function getRiderTripSimilarity(driverTrip, riderTrips) {

    if (typeof riderTrips === "undefined") {
        return undefined
    }
    /* get the driver user matching the username */
    let driverUser;
    UserStore.find({}, (err, users) => {
        if (err) throw err;
        driverUser = users.filter(user => {
            return user.username == driverTrip.username;
        })});

    for (let i = 0; i < riderTrips.length; i++) {
        /* get the rider user matching the username */
        let riderUser;
        UserStore.find({}, (err, users) => {
            if (err) throw err;
            riderUser = users.filter(user => {
                return user.username == riderTrips[i].username;
            })});

        riderTrips[i].similarityWithDriver = getInterestSimilarity(driverUser, riderUser);
    }

    riderTrips = riderTrips.sort(function (a, b) {
        return b.similarityWithDriver - a.similarityWithDriver
    })

    riderTrips = riderTrips.slice(0, 4)

    return riderTrips


}

/*
 * Given two users, accesses their interest fields
 * and computes the similarity between their
 * interests using the Cosine similarity
 */ 
function getInterestSimilarity(user1, user2) {
    let similarity = 1;
    let magA = 0;
    let magB = 0;

    /* COSINE MATCHING FUNCTION */
    for (let i = 0; i < NumInterests; i++) {
        similarity += user1.interests[i] * user2.interests[i];
        magA += Math.pow(user1.interests[i], 2);
        magB += Math.pow(user2.interests[i], 2);
    }

    magA = Math.pow(magA, 0.5);
    magB = Math.pow(magB, 0.5);
    similarity /= magA;
    similarity /= magB;

    return similarity;
}

/*
 * Given a valid driver trip, finds rider trips that 
 * are reasonable for the driver
 */
function getRiderTrips(driverTrip) {
    let riderTrips;

    TripStore.find({}, (err, trips) => {
        if (err) console.log(err);
        riderTrips = trips.filter(trip => {
            return !trip.isDriverTrip;
        })});

    console.log("113")

    //riderTrips = cutTripsByTime(driverTrip, riderTrips);
    console.log("123")
    //riderTrips = cutTripsByBearing(driverTrip, riderTrips);
    console.log("133")
    //riderTrips = cutTripsByDistance(driverTrip, riderTrips);
    console.log("143")

    return riderTrips;
}

/* TODO: port to functional programming with map-filter-reduce
 * Reduces down rider trips to those within some time constraint
 * of driver trip
 */
function cutTripsByTime(driverTrip, riderTrips) {
    let riderTripsTime = [];

    console.log("104")

    if (typeof riderTrips === "undefined") {
        return undefined
    }

    riderTrips.forEach(function(riderTrip, index) {
        if (riderTrip.arrivalTime <= driverTrip.arrivalTime) {
            riderTripsTime.push(riderTrip);
        }
    });

    return riderTripsTime;
}

/* TODO: port to functional programming with map-filter-reduce
 * Reduces down rider trips to those within some bearing constraint
 * of driver trip
 * TESTED WORKING NO EDGE CASE TESTS DONE
 */
function cutTripsByBearing(driverTrip, riderTrips) {
        if (typeof riderTrips === "undefined") {
        return undefined
    }

    let driverBearing = getLatLngBearing(driverTrip.routes[0].legs[0].start_location.lat, 
            driverTrip.routes[0].legs[0].start_location.lng, 
            driverTrip.routes[0].legs[0].end_location.lat, 
            driverTrip.routes[0].legs[0].end_location.lng);
    let riderTripsBearing = [];



    riderTrips.forEach(function(riderTrip, index) {
        var riderBearing = getLatLngBearing(riderTrip.routes[0].legs[0].start_location.lat, 
                riderTrip.routes[0].legs[0].start_location.lng, 
                riderTrip.routes[0].legs[0].end_location.lat, 
                riderTrip.routes[0].legs[0].end_location.lng);
        if (Math.abs(driverBearing - riderBearing) < MaxDriverBearingDiff) {
            riderTripsBearing.push(riderTrip);
        }
    });

    return riderTripsBearing;
}

/* TODO: port to functional programming with map-filter-reduce
 * Reduces down rider trips to those within some distance
 * of driver trip
 * Also guarantees that trips are in the correct direction
 * Also guarantees that trips are in the correct direction
 * TESTED WORKING NO EDGE CASE TESTS DONE
 */ 
function cutTripsByDistance(driverTrip, riderTrips) {
    let riderTripsDistance = [];

        if (typeof riderTrips === "undefined") {
        return undefined
    }

    riderTrips.forEach(function(riderTrip, index) {
        let riderDistanceStart = LatLng.getLatLngShortestDistanceLinePoint(
            driverTrip.routes[0].legs[0].start_location.lat, 
            driverTrip.routes[0].legs[0].start_location.lng, 
            driverTrip.routes[0].legs[0].end_location.lat, 
            driverTrip.routes[0].legs[0].end_location.lng, 
            riderTrip.routes[0].legs[0].start_location.lat, 
            riderTrip.routes[0].legs[0].start_location.lng
        );
        let riderDistanceEnd = LatLng.getLatLngShortestDistanceLinePoint(
            driverTrip.routes[0].legs[0].start_location.lat, 
            driverTrip.routes[0].legs[0].start_location.lng, 
            driverTrip.routes[0].legs[0].end_location.lat, 
            driverTrip.routes[0].legs[0].end_location.lng, 
            riderTrip.routes[0].legs[0].end_location.lat, 
            riderTrip.routes[0].legs[0].end_location.lng
        );

        if (riderDistanceStart + riderDistanceEnd < MaxDriverDistanceDiff) {
            riderTripsDistance.push(riderTrip);
        }
    });

    return riderTripsDistance;
}

module.exports = {
    driverTripHandler,
    tripHandler
}