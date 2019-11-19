
const debug = require("debug")("recommender");

const LatLng = require("./latlng.js");

// database
const mongoose = require("mongoose");
const TripStore = require("../Trip/models/Trip");
const UserStore = require("../User/models/user");

const MaxDriverBearingDiff = 20; // 20 deg
const MaxDriverDistanceDiff = 2000; // 1km

const Directions = require("./directions.js");
const getDirections = Directions.getDirections;
const getDirectionsWithWaypoints = Directions.getDirectionsWithWaypoints;

/* TODO: port to functional programming with map-filter-reduce
 * Reduces down rider trips to those within some time constraint
 * of driver trip
 */
// function cutTripsByTime(driverTrip, riderTrips) {
// 	let riderTripsTime = [];

// 	if (typeof riderTrips === "undefined") {
// 		return [];
// 	}

// 	riderTrips.forEach(function(riderTrip, index) {
// 		if (riderTrip.arrivalTime <= driverTrip.arrivalTime) {
// 			riderTripsTime.push(riderTrip);
// 		}
// 	});

// 	return riderTripsTime;
// }

/* TODO: port to functional programming with map-filter-reduce
 * Reduces down rider trips to those within some bearing constraint
 * of driver trip
 * TESTED WORKING NO EDGE CASE TESTS DONE
 */
function cutTripsByBearing(driverTrip, riderTrips) {
	if (typeof riderTrips === undefined || typeof driverTrip === "undefined" || riderTrips === []) {
		return [];
	}

	let newDriverRoute = driverTrip.tripRoute;
	let driverBearing = LatLng.getLatLngBearing(newDriverRoute.routes[0].legs[0].start_location.lat,
			newDriverRoute.routes[0].legs[0].start_location.lng,
			newDriverRoute.routes[0].legs[0].end_location.lat,
			newDriverRoute.routes[0].legs[0].end_location.lng);
	let riderTripsBearing = [];

	riderTrips.forEach(function(riderTrip) {
		let newRiderRoute = riderTrip.tripRoute;
		let riderBearing = LatLng.getLatLngBearing(newRiderRoute.routes[0].legs[0].start_location.lat,
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

	if (typeof riderTrips === "undefined" || driverTrip === "undefined") {
		return [];
	}

	let newDriverRoute = driverTrip.tripRoute;

	riderTrips.forEach(function(riderTrip) {
		let newRiderRoute = riderTrip.tripRoute;
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
 * Modify the driver trip by adding waypoints for each
 * rider trip start and end
 */
function modifyTrip(driverTrip, riderTrips, callback) {
	if (riderTrips.length < 1 || riderTrips === undefined) {
		callback(driverTrip.tripRoute);
		return;
	}
	let waypoints = [];
	debug("modify trip drivers", driverTrip);
	debug("modify trip riders", riderTrips);
	riderTrips.forEach(function(riderTrip) {
		let startPoint = riderTrip.tripRoute.routes[0].legs[0].start_location.lat + "," + riderTrip.tripRoute.routes[0].legs[0].start_location.lng;
		let endPoint = riderTrip.tripRoute.routes[0].legs[0].end_location.lat + "," + riderTrip.tripRoute.routes[0].legs[0].end_location.lng;
		waypoints.push({location: startPoint, stopover: false});
		waypoints.push({location: endPoint, stopover: false});
	});

	let driverStartPoint = driverTrip.tripRoute.routes[0].legs[0].start_location.lat + "," + driverTrip.tripRoute.routes[0].legs[0].start_location.lng;
	let driverEndPoint = driverTrip.tripRoute.routes[0].legs[0].end_location.lat + "," + driverTrip.tripRoute.routes[0].legs[0].end_location.lng;
	let req = {
		origin: driverStartPoint,
		destination: driverEndPoint,
		waypoints
	};
	debug("req to directions with waypoints:", req);	
	getDirectionsWithWaypoints(req, function(err, res) {
		if (err) {
			debug(err);
			throw err;
		}
		callback(res);
	});
}

/*
 * Given two users, accesses their interest fields
 * and computes the similarity between their
 * interests using the Cosine similarity
 */
function getInterestSimilarity(user1, user2) {
	if (user1.interests === undefined || user2.interests === undefined || user1.interests.length != 5 || user2.interests.length != 5) {
		throw new RangeError("RangeError: user interests invalid");
	}

	let similarity = 1;
	let magA = 0;
	let magB = 0;

	/* COSINE MATCHING FUNCTION */
	const NumInterests = 5;
	for (let i = 0; i < NumInterests; i++) {
		similarity += user1.interests[parseInt(i, 10)] * user2.interests[parseInt(i, 10)];
		magA += Math.pow(user1.interests[parseInt(i, 10)], 2);
		magB += Math.pow(user2.interests[parseInt(i, 10)], 2);
	}

	magA = Math.pow(magA, 0.5);
	magB = Math.pow(magB, 0.5);
	similarity /= magA;
	similarity /= magB;

	debug(similarity);

	return similarity;
}

/*
 * Adds a field to riderTrip indicating similarity to given driver
 */
async function getRiderTripSimilarity(driverTrip, riderTrips, callback) {

	if (typeof riderTrips === "undefined") {
		callback([]);
		return;
	}
	if (typeof driverTrip === "undefined") {
		callback([]);
		return;
	}
	/* get the driver user matching the username */
	let driverUser;

	await UserStore.findById(driverTrip.userID, (err, user) => {
		if (err) {
			debug(err);
		} else {
			driverUser = user;
			debug(driverUser);
		}
	});

	for (const riderTrip of riderTrips) {
		let riderUser;
		await UserStore.findById(riderTrip.userID, (err, user) => {
			if (err) {
				debug(err);
			} else {
				riderUser = user;
				debug(riderUser);
			}
		});
		if (riderUser === null || typeof riderUser === "undefined" || typeof riderUser.interests === "undefined" || typeof driverUser.interests === "undefined") {
			riderTrips = riderTrips.filter((value, index, arr) => {
				return value != riderTrip;
			});
		} else {
			riderTrip.similarityWithDriver = getInterestSimilarity(driverUser, riderUser);
		}
	}
	
	riderTrips = riderTrips.sort(function (a, b) {
		return b.similarityWithDriver - a.similarityWithDriver;
	});

	debug(riderTrips, "OK");
	callback(riderTrips);
}

/*
 * Given a valid driver trip, finds rider trips that
 * are reasonable for the driver
 */
async function getRiderTrips(driverTrip, callback) {
	if (typeof driverTrip === "undefined") {
		callback([]);
		return;
	}
	
	let riderTrips;

	await TripStore.find({}, (err, trips) => {
		if (err) {
			debug(err);
		}
		riderTrips = trips.filter((trip) => {
			return !(trip.isDriverTrip || trip.isFulfilled);
		});
	});
	debug("raw rider trips:", riderTrips);

	//riderTrips = cutTripsByTime(driverTrip, riderTrips);
	riderTrips = cutTripsByBearing(driverTrip, riderTrips);
	riderTrips = cutTripsByDistance(driverTrip, riderTrips);

	callback(riderTrips);
}

/*
 * Handles a driver trip request
 * Gets the applicable rider trips
 */
function driverTripHandler(driverTrip, callback) {
	getRiderTrips(driverTrip, function(riderTrips) {
		debug("riderTrips:", riderTrips);
		riderTrips = getRiderTripSimilarity(driverTrip, riderTrips, function(riderTrips) {
			callback(riderTrips, driverTrip);
		});
	});
}

/*
 * Handles all trips
 * Gets the directions object from Google
 *
 */
function tripHandler(trip, callback) {
	let startPoint = trip.origin;
	let endPoint = trip.destination;
	let req = {
		origin: startPoint,
		destination: endPoint
	};
	getDirections(req, function(err, res) {
		if (err) {
			debug(err);
			throw err;
		}
		callback(res);
	});
}

module.exports = {
	driverTripHandler,
	tripHandler,
	cutTripsByBearing,
	cutTripsByDistance,
	modifyTrip,
	getInterestSimilarity,
	getRiderTripSimilarity,
	getRiderTrips
};
