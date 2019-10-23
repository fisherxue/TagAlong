
const LatLng = require('./latlng.js');

// database
const TripStore = require('../Trip/models/Trip');
const UserStore = require('../User/models/User');

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
    googleMapsClient.directions({
        origin: req.origin,
        destination: req.destination,
    }, function(err, response) {
        if (err) throw err;
        callback(response);
    });
};

function driverTripHandler(driverTrip) {
    let riderTrips = getRiderTrips(driverTrip);
    riderTrips = getRiderTripSimilarity(driverTrip, riderTrips);
    return riderTrips;
}

function riderTripHandler(riderTrip, callback) {
    let startPoint = riderTrip.startLat + ',' + riderTrip.startLng;
    let endPoint = riderTrip.endLat + ',' + riderTrip.endLng
    let req = {
        origin: startPoint,
        destination: endPoint
    }
    callback(getDirections(req, function(res) {
        callback(res);
    }));
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
    /* get the driver user matching the username */
    let driverUser;
    UserStore.find({}, (err, users) => {
        if (err) throw err;
        driverUser = users.filter(user => {
            return user.username == driverTrip.username;
        })
    });

    for (let i = 0; i < riderTrips.length; i++) {
        /* get the rider user matching the username */
        let riderUser;
        UserStore.find({}, (err, users) => {
            if (err) throw err;
            riderUser = users.filter(user => {
                return user.username == riderTrips[i].username;
            })
        });

        riderTrips[i].similarityWithDriver = getInterestSimilarity(driverUser, riderUser);
    }
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
        if (err) throw err;
        riderTrips = trips.filter(trip => {
            return !trip.isDriverTrip;
        });

    riderTrips = cutTripsByTime(driverTrip, riderTrips);
    riderTrips = cutTripsByBearing(driverTrip, riderTrips);
    riderTrips = cutTripsByDistance(driverTrip, riderTrips);

    return riderTrips;
}

/* TODO: port to functional programming with map-filter-reduce
 * Reduces down rider trips to those within some time constraint
 * of driver trip
 */
function cutTripsByTime(driverTrip, riderTrips) {
    let riderTripsTime = [];

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
    let driverBearing = getLatLngBearing(driverTrip.startLat, 
            driverTrip.startLng, driverTrip.endLat, driverTrip.endLng);
    let riderTripsBearing = [];

    riderTrips.forEach(function(riderTrip, index) {
        var riderBearing = getLatLngBearing(riderTrip.startLat, riderTrip.startLng, 
            riderTrip.endLat, riderTrip.endLng);
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

    riderTrips.forEach(function(riderTrip, index) {
        let riderDistanceStart = LatLng.getLatLngShortestDistanceLinePoint(
            driverTrip.startLat, driverTrip.startLng, driverTrip.endLat,
            driverTrip.endLng, riderTrip.startLat, riderTrip.startLng
        );
        let riderDistanceEnd = LatLng.getLatLngShortestDistanceLinePoint(
            driverTrip.startLat, driverTrip.startLng, driverTrip.endLat,
            driverTrip.endLng, riderTrip.endLat, riderTrip.endLng
        );

        if (riderDistanceStart + riderDistanceEnd < MaxDriverDistanceDiff) {
            riderTripsDistance.push(riderTrip);
        }
    });

    return riderTripsDistance;
}
