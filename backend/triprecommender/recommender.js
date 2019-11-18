
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
	if (typeof riderTrips === "undefined") {
		return [];
	}

	let newDriverRoute = JSON.parse(driverTrip.tripRoute);
	let driverBearing = LatLng.getLatLngBearing(newDriverRoute.routes[0].legs[0].start_location.lat,
			newDriverRoute.routes[0].legs[0].start_location.lng,
			newDriverRoute.routes[0].legs[0].end_location.lat,
			newDriverRoute.routes[0].legs[0].end_location.lng);
	let riderTripsBearing = [];

	riderTrips.forEach(function(riderTrip) {
		let newRiderRoute = JSON.parse(riderTrip.tripRoute);
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

	if (typeof riderTrips === "undefined") {
		return [];
	}

	let newDriverRoute = JSON.parse(driverTrip.tripRoute);

	riderTrips.forEach(function(riderTrip) {
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
 * Modify the driver trip by adding waypoints for each
 * rider trip start and end
 */
function modifyTrip(driverTrip, riderTrips, callback) {
	let waypoints = [];

	riderTrips.forEach(function(riderTrip) {
		let startPoint = riderTrip.startLat + "," + riderTrip.startLng;
		let endPoint = riderTrip.endLat + "," + riderTrip.endLng;
		waypoints.push(startPoint);
		waypoints.push(endPoint);
	});

	let driverStartPoint = driverTrip.startLat + "," + driverTrip.startLng;
	let driverEndPoint = driverTrip.endLat + "," + driverTrip.endLng;
	let req = {
		origin: driverStartPoint,
		destination: driverEndPoint,
		waypoints
	};

	getDirectionsWithWaypoints(req, function(err, res) {
		callback(res);
	});
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
		similarity += user1.interests[parseInt(i, 10)] * user2.interests[parseInt(i, 10)];
		magA += Math.pow(user1.interests[parseInt(i, 10)], 2);
		magB += Math.pow(user2.interests[parseInt(i, 10)], 2);
	}

	magA = Math.pow(magA, 0.5);
	magB = Math.pow(magB, 0.5);
	similarity /= magA;
	similarity /= magB;

	return similarity;
}

/*
 * Adds a field to riderTrip indicating similarity to given driver
 */
async function getRiderTripSimilarity(driverTrip, riderTrips, callback) {

	if (typeof riderTrips === "undefined") {
		callback([]);
	}
	/* get the driver user matching the username */
	let driverUser;

	if (mongoose.Types.ObjectId.isValid(driverTrip.userID)) {
		await UserStore.findById(driverTrip.userID, (err, user) => {
			if (err) {
				debug(err);
			} else {
				driverUser = user;
				debug(driverUser);
			}
		});
	} else {
		debug("invalid user id from driver trip");
		debug(typeof driverTrip.userID, driverTrip.userID);
	}

	riderTrips.forEach(async function(riderTrip) {
		/* get the rider user matching the username */
		let riderUser;
		if (mongoose.Types.ObjectId.isValid(riderTrip.userID)) {
			await UserStore.findById(riderTrip.userID, (err, user) => {
				if (err) {
					debug(err);
				} else {
					riderUser = user;
					debug(riderUser);
				}
			});
			riderTrip.similarityWithDriver = getInterestSimilarity(driverUser, riderUser);
		} else {
			debug("invalid user id from rider trip");
			debug(typeof riderTrip.userID, riderTrip.userID);
		}
	});

	riderTrips = riderTrips.sort(function (a, b) {
		return b.similarityWithDriver - a.similarityWithDriver;
	});

	debug(riderTrips, "OK");

	riderTrips = riderTrips.slice(0, 1); // should slice by driver car size

	let tripID = riderTrips[0]._id;
	let update = riderTrips[0];

	update.isFulfilled = true;

	await TripStore.findByIdAndUpdate(tripID, update, {new: true}, (err) => {
		if (err) {
			debug(err);
		}
	});

	callback(riderTrips);
}

/*
 * Given a valid driver trip, finds rider trips that
 * are reasonable for the driver
 */
async function getRiderTrips(driverTrip, callback) {
	let riderTrips;

	await TripStore.find({}, (err, trips) => {
		if (err) {
			debug(err);
		}
		riderTrips = trips.filter((trip) => {
			return !(trip.isDriverTrip || trip.isFulfilled);
		});
	});

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
		riderTrips = getRiderTripSimilarity(driverTrip, riderTrips, function(riderTrips) {
			modifyTrip(driverTrip, riderTrips, (res) => {
				callback(riderTrips, driverTrip);
			});
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
	tripHandler
};
