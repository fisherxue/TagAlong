const googleMapsKey = require('./keys.js').GoogleMapsJavascriptAPIKey;
// initialize maps client
const googleMapsClient = require('@google/maps').createClient({
    key: googleMapsKey
});

const MaxDriverBearingDiff = 20; // 20 deg
MaxDriverDistanceDiff = 1000; // 1km

import {
    getLatLngDistance, 
    getLatLngBearing, 
    getLatLngShortestDistanceLinePoint
} from './latlng.js';

/* 
 * Gets all rider trips: highly suboptimal, but works for now
 */ 
function getAvailableRiderTrips(driverTrip, callback) {
    /*
     * DB communication
     */
	callback(trips);
}

/*
 * Define a trip object as
 * arrivalTime
 * userID
 * start_lat, start_lng
 * end_lat, end_lng
 * distance
 * weight
 * length
 * Google Maps directions API output: route
 */

function findOptimalTrip(driverTrip, riderTrips) {
    riderTrips = cutTripsByTime(driverTrip, riderTrips);
    riderTrips = cutTripsByBearing(driverTrip, riderTrips);
    riderTrips = cutTripsByDistance(driverTrip, riderTrips);
    preprocessRiderTripWeights(riderTrips);
    var sortedRiderTrips = riderTrips.sort(function(a, b) {
        return a.arrivalTime - b.arrivalTime;
    });
}

/* TODO: port to functional programming with map-filter-reduce
 * Reduces down rider trips to those within some time constraint
 * of driver trip
 */
function cutTripsByTime(driverTrip, riderTrips) {
    var riderTripsTime = [];

    riderTrips.forEach(function(riderTrip, index) {
        if (riderTrip.arrivalTime < driverTrip.arrivalTime) {
            riderTripsTime.push(riderTrip);
        }
    });

    return riderTripsTime;
}

/* TODO: port to functional programming with map-filter-reduce
 * Reduces down rider trips to those within some bearing constraint
 * of driver trip
 */
function cutTripsByBearing(driverTrip, riderTrips) {
    var driverBearing = getLatLngBearing(driverTrip.start_lat, 
            driverTrip.end_lat, driverTrip.start_lng, driverTrip.end_lng);
    var riderTripsBearing = [];

    riderTrips.forEach(function(riderTrip, index) {
        var riderBearing = getLatLngBearing(riderTrip.start_lat, riderTrip.end_lat, 
            riderTrip.start_lng, riderTrip.end_lng);
        if (Math.abs(driverBearing - riderBearing) < MaxDriverBearingDiff) {
            riderTripsBearing.push(riderTrip);
        }
    });

    return riderTripsBearing;
}

/* TODO: port to functional programming with map-filter-reduce
 * Reduces down rider trips to those within some distance
 * of driver trip
 */ 
function cutTripsByDistance(driverTrip, riderTrips) {
    var riderTripsDistance = [];

    riderTrips.forEach(function(riderTrip, index) {
        var riderDistanceStart = getLatLngShortestDistanceLinePoint(
            driverTrip.start_lat, driverTrip.start_lng, driverTrip.end_lat,
            driverTrip.end_lng, riderTrip.start_lat, riderTrip.start_lng
        );
        var riderDistanceEnd = getLatLngShortestDistanceLinePoint(
            driverTrip.start_lat, driverTrip.start_lng, driverTrip.end_lat,
            driverTrip.end_lng, riderTrip.end_lat, riderTrip.end_lng
        );

        if (riderDistanceStart + riderDistanceEnd < MaxDriverDistanceDiff) {
            riderTripsDistance.push(riderTrip);
        }
    });

    return riderTripsDistance;
}

function preprocessRiderTripWeights(driverTrip, riderTrips) {
    riderTrips.forEach(function(riderTrip, index) {
        riderTrip.weight = riderTrip.distance * getSimilarity(driverTrip.user, riderTrip.user);
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

