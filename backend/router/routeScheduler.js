const LatLng = require('./latlng.js');
const getLatLngDistance = LatLng.getLatLngDistance;
const getLatLngBearing = LatLng.getLatLngBearing;
const getLatLngShortestDistanceLinePoint = LatLng.getLatLngShortestDistanceLinePoint;

const Directions = require('./directions.js');
const getDirectionsWithWaypoints = Directions.getDirectionsWithWaypoints;

const fs = require('fs')

const MaxDriverBearingDiff = 20; // 20 deg
const MaxDriverDistanceDiff = 1000; // 1km

/*
 * Trip wrapper object: extract the relevant data from trip
 *
 */ 
function Trip(arrivalTime, userID, tripID, startLat, startLng, endLat, endLng, distance, duration) {
    this.arrivalTime = arrivalTime;
    this.userID = userID;   
    this.tripID = tripID;
    this.startLat = startLat;
    this.startLng = startLng;
    this.endLat = endLat;
    this.endLng = endLng;
    this.distance = distance;
    this.duration = duration;
}

/* 
 * Gets all rider trips: highly suboptimal, but works for now
 */ 
function getAvailableRiderTrips(driverTrip, callback) {
    /*
     * DB communication
     */

    let trips = new Array();
    let tripTimes = new Array();

    tripTimes[0] = new Date("2019-10-05T15:00:00.000Z");
    tripTimes[1] = new Date("2019-10-05T14:48:00.000Z");
    tripTimes[2] = new Date("2019-10-05T15:00:00.000Z");
    trips.push(JSON.parse(fs.readFileSync('indigotonest.json', 'utf8')));
    trips.push(JSON.parse(fs.readFileSync('indigotobyng.json', 'utf8')));
    trips.push(JSON.parse(fs.readFileSync('byngtonest.json', 'utf8')));    

    for (let i = 0; i < trips.length; i++) {
        trips[i] = getTripFromDirectionsJSON(trips[i], tripTimes[i], i, i);
    }

    callback(trips);
}

function getTripFromDirectionsJSON(json, arrivalTime, userID=0, tripID=0) {
    let startLat = json.routes[0].legs[0].start_location.lat;
    let startLng = json.routes[0].legs[0].start_location.lng;
    let endLat = json.routes[0].legs[0].end_location.lat;
    let endLng = json.routes[0].legs[0].end_location.lng;
    let distance = json.routes[0].legs[0].distance.value;
    let duration = json.routes[0].legs[0].duration.value;
    let trip = new Trip(arrivalTime, userID, tripID, startLat, startLng, endLat, endLng, distance, duration);
    return trip;
}   


function routeSchedulerRequestHandler(driverTrip, callback) {
    getAvailableRiderTrips(driverTrip, function(trips) {
        let driverTripJSON = JSON.parse(fs.readFileSync('indigotonest.json', 'utf8'));
        let driverTripTime = new Date("2019-10-05T15:00:00.000Z");
        let driverTrip = getTripFromDirectionsJSON(driverTripJSON, driverTripTime, 42, 42);
        let optimalTrip = findOptimalTrip(driverTrip, trips);
        console.log(optimalTrip);
        let waypoints = [];
        
        let driverStartPoint = driverTrip.startLat + ',' + driverTrip.startLng;
        let driverEndPoint = driverTrip.endLat + ',' + driverTrip.endLng;

        for (let i = 0; i < optimalTrip.length; i++) {
            let startPoint = optimalTrip[i].startLat + ',' + optimalTrip[i].startLng;
            let endPoint = optimalTrip[i].endLat + ',' + optimalTrip[i].endLng
            if (!waypoints.includes(startPoint) & driverStartPoint != startPoint) waypoints.push(startPoint);
            if (!waypoints.includes(startPoint) & driverEndPoint != endPoint) waypoints.push(endPoint);
        }
        console.log(waypoints)

        let req = {
            origin: driverStartPoint,
            destination: driverEndPoint,
            waypoints: waypoints
        };

        let userIDs = [];

        for (let i = 0; i < optimalTrip.length; i++) {
            userIDs.push(optimalTrip[i].userID);
        }
        
        getDirectionsWithWaypoints(req, function(err, response) {
            if (err) throw err;
            callback(response, driverTrip, userIDs);
        });
    });
}

routeSchedulerRequestHandler(null, null);

/*
 * Define a trip object as
 * arrivalTime
 * userID
 * startLat, startLng // from big boi JSON
 * endLat, endLng // from big boi JSON
 * distance // from big boi JSON
 * weight // i compute
 * Google Maps directions API output: route big boi JSON
 * New Google maps directions API output for route WITH rider shit
 */

 /*
  * Complex logic: finds optimal trip for driver trip
  *
  */ 
function findOptimalTrip(driverTrip, riderTrips) {
    riderTrips = cutTripsByTime(driverTrip, riderTrips);
    riderTrips = cutTripsByBearing(driverTrip, riderTrips);
    riderTrips = cutTripsByDistance(driverTrip, riderTrips);
    preprocessRiderTripWeights(driverTrip, riderTrips);
    var sortedRiderTrips = riderTrips.sort(function(a, b) {
        return a.arrivalTime - b.arrivalTime;
    });

    let optimalTrip = [];
    let scheduling = new Array(riderTrips.length); // array our algorithm operates on
    scheduling[0] = riderTrips[0].weight;
    
    /* Create the prev array that tracks the latest trip that won't conflict: O(n^2) */
    let prev = new Array();
    for (let i = 0; i < riderTrips.length; i++) {
        prev[i] = -1;
        for (let j = 0; j < i; j++) {
            let dateTemp = new Date(riderTrips[i].arrivalTime);
            dateTemp.setSeconds(dateTemp.getSeconds() - riderTrips[i].duration);
            if (riderTrips[j].arrivalTime <= dateTemp) {
                prev[i] = j;
            }
        }
    }

    scheduling[0] = 0;
    for (let i = 1; i <= riderTrips.length; i++) {
        let ip = i - 1;
        scheduling[i] = Math.max(riderTrips[ip].weight + scheduling[prev[ip] + 1], scheduling[ip]);
    }

    for (let i = riderTrips.length; i > 0;) {
        let ip = i - 1;
        if (scheduling[i] > scheduling[i - 1]) {
            optimalTrip.push(riderTrips[ip]);
            i = prev[ip] + 1;
        } else {
            i--;
        }
    }

    return optimalTrip.reverse();
}

/* TODO: port to functional programming with map-filter-reduce
 * Reduces down rider trips to those within some time constraint
 * of driver trip
 */
function cutTripsByTime(driverTrip, riderTrips) {
    var riderTripsTime = [];

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
    var driverBearing = getLatLngBearing(driverTrip.startLat, 
            driverTrip.startLng, driverTrip.endLat, driverTrip.endLng);
    var riderTripsBearing = [];

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
        let riderDistanceStart = getLatLngShortestDistanceLinePoint(
            driverTrip.startLat, driverTrip.startLng, driverTrip.endLat,
            driverTrip.endLng, riderTrip.startLat, riderTrip.startLng
        );
        let riderDistanceEnd = getLatLngShortestDistanceLinePoint(
            driverTrip.startLat, driverTrip.startLng, driverTrip.endLat,
            driverTrip.endLng, riderTrip.endLat, riderTrip.endLng
        );

        /*
        let riderStartDistanceToDriverDest = getLatLngDistance(
            riderTrip.startLat, riderTrip.startLng, 
            driverTrip.endLat, driverTrip.endLng
        );

        let riderEndDistanceToDriverDest = getLatLngDistance(
            riderTrip.endLat, riderTrip.endLng, 
            driverTrip.endLat, driverTrip.endLng
        );
        */

        if (riderDistanceStart + riderDistanceEnd < MaxDriverDistanceDiff) {
            riderTripsDistance.push(riderTrip);
        }
    });

    return riderTripsDistance;
}

function preprocessRiderTripWeights(driverTrip, riderTrips) {
    riderTrips.forEach(function(riderTrip, index) {
        riderTrip.weight = riderTrip.distance * getSimilarity(driverTrip.userID, riderTrip.userID);
    });
}

function getSimilarity(user1, user2) {
    var similarity = 1;
    /*
    foreach interest in user1.interests:
        if interest in user2.interests:
            similarity += 1;
    */
   return similarity;
}
