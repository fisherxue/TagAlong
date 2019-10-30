
const LatLng = require("./latlng.js");

// database
const TripStore = require("../Trip/models/Trip");
const UserStore = require("../User/models/user");

/* Google Maps */
// initialize maps client
const googleMapsClient = require("@google/maps").createClient({
    key: "AIzaSyDkjse1zwmX7lw71D5wpKIP0xrbKLG1YIQ"
});


const MaxDriverBearingDiff = 20; // 20 deg
const MaxDriverDistanceDiff = 2000; // 1km
const NumInterests = 5;

/* Get directions with waypoints */
function getDirectionsWithWaypoints(req, callback) {
    googleMapsClient.directions({
        origin: req.origin,
        destination: req.destination,
        waypoints: req.waypoints,
    }, function(err, response) {
        if (err) {
            throw err;
        }

        callback(response);
    });
}
/* Get directions */
function getDirections(req, callback) {
    googleMapsClient.directions({
        origin: req.origin,
        destination: req.destination,
    }, function(err, response) {
        if (err) {
            console.log(err);
        }
        callback(response);
    });
}

/* 
 * Handles a driver trip request 
 * Gets the applicable rider trips
 */
function driverTripHandler(driverTrip, callback) {
    getRiderTrips(driverTrip, function(riderTrips) {
        riderTrips = getRiderTripSimilarity(driverTrip, riderTrips, function(riderTrips) {
            modifyTrip(driverTrip, riderTrips, (res) => {
                callback(riderTrips, driverTrip);
            })
        }); 
    });
}


/* TODO: port to functional programming with map-filter-reduce
 * Reduces down rider trips to those within some time constraint
 * of driver trip
 */
function cutTripsByTime(driverTrip, riderTrips) {
    let riderTripsTime = [];

    if (typeof riderTrips === "undefined") {
        return undefined;
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
        return undefined;
    }

    let newDriverRoute = JSON.parse(driverTrip.tripRoute);

    let driverBearing = LatLng.getLatLngBearing(newDriverRoute.routes[0].legs[0].start_location.lat, 
            newDriverRoute.routes[0].legs[0].start_location.lng, 
            newDriverRoute.routes[0].legs[0].end_location.lat, 
            newDriverRoute.routes[0].legs[0].end_location.lng);
    let riderTripsBearing = [];



    riderTrips.forEach(function(riderTrip, index) {
        let newRiderRoute = JSON.parse(riderTrip.tripRoute);
        var riderBearing = LatLng.getLatLngBearing(newRiderRoute.routes[0].legs[0].start_location.lat, 
                newRiderRoute.routes[0].legs[0].start_location.lng, 
                newRiderRoute.routes[0].legs[0].end_location.lat, 
                newRiderRoute.routes[0].legs[0].end_location.lng);
        if (Math.abs(driverBearing - riderBearing) < MaxDriverBearingDiff) {
            riderTripsBearing.push(riderTrip);
        }
    });

    return riderTripsBearing;
}

/* TODO: port to functional programming with map-filter-reduce
 * Reduces down rider trips to those within some distance
 * of driver trip
 * TESTED WORKING NO EDGE CASE TESTS DONE
 */ 
function cutTripsByDistance(driverTrip, riderTrips) {
    let riderTripsDistance = [];

    if (typeof riderTrips === "undefined") {
        return undefined;
    }

    let newDriverRoute = JSON.parse(driverTrip.tripRoute);

    riderTrips.forEach(function(riderTrip, index) {
        let newRiderRoute = JSON.parse(riderTrip.tripRoute);
        let riderDistanceStart = LatLng.getLatLngShortestDistanceLinePoint(
            newDriverRoute.routes[0].legs[0].start_location.lat, 
            newDriverRoute.routes[0].legs[0].start_location.lng, 
            newDriverRoute.routes[0].legs[0].end_location.lat, 
            newDriverRoute.routes[0].legs[0].end_location.lng, 
            newRiderRoute.routes[0].legs[0].start_location.lat, 
            newRiderRoute.routes[0].legs[0].start_location.lng
        );
        let riderDistanceEnd = LatLng.getLatLngShortestDistanceLinePoint(
            newDriverRoute.routes[0].legs[0].start_location.lat, 
            newDriverRoute.routes[0].legs[0].start_location.lng, 
            newDriverRoute.routes[0].legs[0].end_location.lat, 
            newDriverRoute.routes[0].legs[0].end_location.lng, 
            newRiderRoute.routes[0].legs[0].end_location.lat, 
            newRiderRoute.routes[0].legs[0].end_location.lng
        );

        if (riderDistanceStart + riderDistanceEnd < MaxDriverDistanceDiff) {
            riderTripsDistance.push(riderTrip);
        }
    });

    return riderTripsDistance;
}

/*
 * Handles all trips
 * Gets the directions object from Google
 *
 */
function tripHandler(trip, callback) {
    let startPoint = trip.origin;
    let endPoint = trip.destination
    let req = {
        origin: startPoint,
        destination: endPoint
    }
    getDirections(req, function(res) {
        callback(res);
    });
}

/*
 * Modify the driver trip by adding waypoints for each 
 * rider trip start and end
 */ 
function modifyTrip(driverTrip, riderTrips, callback) {
    let waypoints = [];

    riderTrips.forEach(function(riderTrip) {
        let startPoint = riderTrip.startLat + "," + riderTrip.startLng;
        let endPoint = riderTrip.endLat + "," + riderTrip.endLng
        waypoints.push(startPoint);
        waypoints.push(endPoint);
    });
    

    let driverStartPoint = driverTrip.startLat + "," + driverTrip.startLng;
    let driverEndPoint = driverTrip.endLat + "," + driverTrip.endLng;
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
async function getRiderTripSimilarity(driverTrip, riderTrips, callback) {

    if (typeof riderTrips === "undefined") {
        return undefined;
    }
    /* get the driver user matching the username */
    let driverUser;
    await UserStore.findById(driverTrip.userID, (err, user) => {
        driverUser = user;
    });

    riderTrips.forEach(function(riderTrip) {
         /* get the rider user matching the username */
         let riderUser;
         await UserStore.findById(riderTrip.userID, (err, user) => {
             riderUser = user;
         });
 
         riderTrip.similarityWithDriver = getInterestSimilarity(driverUser, riderUser);
    });

    riderTrips = riderTrips.sort(function (a, b) {
        return b.similarityWithDriver - a.similarityWithDriver;
    })

    console.log(riderTrips, "OK");

    riderTrips = riderTrips.slice(0, 1); // should slice by driver car size

    riderTrips[0].isFulfilled = true;

    let userID = riderTrips[0].userID;
    let update = riderTrips[0];

    await TripStore.findByIdAndUpdate(userID, update, {new: true}, (err) => {
        if (err) {
            console.log(err);
        }
    });

    callback(riderTrips);


}

/*
 * Given two users, accesses their interest fields
 * and computes the similarity between their
 * interests using the Cosine similarity
 */ 
function getInterestSimilarity(user1, user2) {
	return 1; // replace this when we reimplement interests

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

    console.log(similarity);

    return similarity;
}

/*
 * Given a valid driver trip, finds rider trips that 
 * are reasonable for the driver
 */
async function getRiderTrips(driverTrip, callback) {
    let riderTrips;

    await TripStore.find({}, (err, trips) => {
        if (err) console.log(err);
        riderTrips = trips.filter(trip => {
            return !(trip.isDriverTrip || trip.isFulfilled);
        })});   


    //riderTrips = cutTripsByTime(driverTrip, riderTrips);
    riderTrips = cutTripsByBearing(driverTrip, riderTrips);
    //riderTrips = cutTripsByDistance(driverTrip, riderTrips);

    callback(riderTrips);
}

module.exports = {
    driverTripHandler,
    tripHandler
};
