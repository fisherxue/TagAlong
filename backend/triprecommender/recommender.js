const debug = require("debug")("recommender");

const LatLng = require("./latlng.js");

// database
const mongoose = require("mongoose");
const TripStore = require("../Trip/models/Trip");
const UserStore = require("../User/models/user");

const MaxDriverBearingDiff = 20; // 20 deg
const MaxDriverDistanceDiff = 2000; // 1km
const MaxDriverTimeDiff = 300;

const Directions = require("./directions.js");
const getDirections = Directions.getDirections;
const getDirectionsWithWaypoints = Directions.getDirectionsWithWaypoints;

/* 
 * Reduces down rider trips to those within some time constraint
 * of driver trip
 * Constraint: Rider trip departs at greater or equal time to driver, 
 * arrives at time less than or equal to driver
 * @param Trip driverTrip: the driver's trip we filter based on
 * @param Trip riderTrips: an array of rider trips we filter
 * @return array of riderTrips after applying this constraint
 */
function cutTripsByTime(driverTrip, riderTrips) {
	if (typeof riderTrips === "undefined" || typeof driverTrip === "undefined" || riderTrips === []) {
		return [];
	}
	let riderTripsTime = [];

	let driverDepartureDate = new Date(driverTrip.arrivalTime);
	driverDepartureDate.setSeconds(driverDepartureDate.getSeconds() - driverTrip.tripRoute.routes[0].legs[0].duration.value - MaxDriverTimeDiff);
	
	driverTrip.arrivalTime = new Date(driverTrip.arrivalTime);

	driverTrip.arrivalTime.setSeconds(driverTrip.arrivalTime.getSeconds() + MaxDriverTimeDiff);
	riderTrips.forEach(function(riderTrip, index) {
		let riderDepartureDate = new Date(riderTrip.arrivalTime);
		riderTrip.arrivalTime = new Date(riderTrip.arrivalTime);
		riderDepartureDate.setSeconds(riderDepartureDate.getSeconds() - riderTrip.tripRoute.routes[0].legs[0].duration.value);
		if (riderTrip.arrivalTime <= driverTrip.arrivalTime && riderDepartureDate >= driverDepartureDate) {
			riderTripsTime.push(riderTrip);
		}
	});

	driverTrip.arrivalTime.setSeconds(driverTrip.arrivalTime.getSeconds() - MaxDriverTimeDiff);


	return riderTripsTime;
}

/* 
 * Reduces down rider trips to those within some bearing constraint
 * of driver trip
 * Constraint: Rider trip and driver trip are less than MaxDriverBearingDiff
 * degrees apart
 * @param Trip driverTrip: the driver's trip we filter based on
 * @param Trip riderTrips: an array of rider trips we filter
 * @return array of riderTrips after applying this constraint
 */
function cutTripsByBearing(driverTrip, riderTrips) {
	if (typeof riderTrips === "undefined" || typeof driverTrip === "undefined" || riderTrips === []) {
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
		if (Math.abs(driverBearing - riderBearing) <= MaxDriverBearingDiff) {
			riderTripsBearing.push(riderTrip);
		}
	});

	return riderTripsBearing;
}

/* 
 * Reduces down rider trips to those within some distance constraint
 * of driver trip
 * Constraint: Rider trip is less than MaxDriverDistanceDiff 
 * @param Trip driverTrip: the driver's trip we filter based on
 * @param Trip riderTrips: an array of rider trips we filter
 * @return array of riderTrips after applying this constraint
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

		if (riderDistanceStart + riderDistanceEnd <= MaxDriverDistanceDiff) {
			riderTripsDistance.push(riderTrip);
		}
	});

	return riderTripsDistance;
}

/*
 * Modify the tripRoute for the driver trip given a set of 
 * rider trips that the driver must accommodate
 * @param Trip driverTrip: the driver's trip 
 * @param Trip riderTrips: array of riderTrips we add to driver Trip
 * @callback JSON triproute: Google Maps result for the trip
 */
function modifyTrip(driverTrip, riderTrips, callback) {
	if (riderTrips.length < 1 || typeof riderTrips === "undefined") {
		callback(driverTrip.tripRoute);
		return;
	}
	let waypoints = [];
	debug("modify trip drivers", driverTrip);
	debug("modify trip riders", riderTrips);
	riderTrips.forEach(function(riderTrip) {
		let startPoint = riderTrip.tripRoute.routes[0].legs[0].start_location.lat + "," + riderTrip.tripRoute.routes[0].legs[0].start_location.lng;
		let endPoint = riderTrip.tripRoute.routes[0].legs[0].end_location.lat + "," + riderTrip.tripRoute.routes[0].legs[0].end_location.lng;
		waypoints.push(startPoint);
		waypoints.push(endPoint);
	});

	let driverStartPoint = driverTrip.tripRoute.routes[0].legs[0].start_location.lat + "," + driverTrip.tripRoute.routes[0].legs[0].start_location.lng;
	let driverEndPoint = driverTrip.tripRoute.routes[0].legs[driverTrip.tripRoute.routes[0].legs.length-1].end_location.lat + "," + driverTrip.tripRoute.routes[0].legs[driverTrip.tripRoute.routes[0].legs.length-1].end_location.lng;
	let req = {
		origin: driverStartPoint,
		destination: driverEndPoint,
		waypoints
	};
	debug("req to directions with waypoints:", req);	


	getDirectionsWithWaypoints(req, (err, res) => {
		callback(res.json);
	});
	return;
}

/*
 * Given two users, accesses their interest fields
 * and computes the similarity between their
 * interests using the Cosine similarity
 * @param User user1: a user with interests
 * @param User user2: a user with interests
 * @return Number similarity: similarity between two users
 */
function getInterestSimilarity(user1, user2) {
	if (typeof user1.interests === "undefined" || typeof user2.interests === "undefined" || user1.interests.length != 5 || user2.interests.length != 5) {
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
 * Gets and sorts an array of riderTrips based on 
 * similarity with the driver
 * @param Trip driverTrip: the driver's trip 
 * @param Trip riderTrips: array of riderTrips
 * @return sorted array of riderTrips
 */
async function getRiderTripSimilarity(driverTrip, riderTrips) {

	if (typeof riderTrips === "undefined") {
		return [];
	}
	if (typeof driverTrip === "undefined") {
		return [];
	}
	/* get the driver user matching the username */
	let driverUser;

	driverUser = await UserStore.findById(driverTrip.userID);

	for (const riderTrip of riderTrips) {
		let riderUser;
		riderUser = await UserStore.findById(riderTrip.userID);
		if (riderUser === null || typeof riderUser === "undefined" || typeof riderUser.interests === "undefined" || typeof driverUser.interests === "undefined") {
			riderTrips = riderTrips.filter((value, index, arr) => {
				return value !== riderTrip;
			});
		} else {
			riderTrip.similarityWithDriver = getInterestSimilarity(driverUser, riderUser);
		}
	}

	
	riderTrips = riderTrips.sort(function (a, b) {
		return b.similarityWithDriver - a.similarityWithDriver;
	});

	debug(riderTrips, "OK");
	return riderTrips;
}

/*
 * Given a valid driver trip, finds rider trips that
 * are reasonable for the driver. 
 * Compatible: within some time, bearing, distance constraint
 * @param driverTrip: the driver's trip
 * @return riderTrips: array of rider trips that are 
 * compatible with driver trip
 */
async function getRiderTrips(driverTrip) {
	if (typeof driverTrip === "undefined") {
		return [];
	}
	
	let riderTrips;

	riderTrips = await TripStore.find({});
	riderTrips = riderTrips.filter((trip) => {
			return !(trip.isDriverTrip || trip.isFulfilled);
		});

	debug("raw rider trips:", riderTrips);
	riderTrips = cutTripsByTime(driverTrip, riderTrips);
	riderTrips = cutTripsByBearing(driverTrip, riderTrips);
	riderTrips = cutTripsByDistance(driverTrip, riderTrips);
	return riderTrips;
}

/*
 * Handles a driver trip request
 * Gets the applicable rider trips sorted by similarity
 */
async function driverTripHandler(driverTrip) {
	if (typeof driverTrip === "undefined") {
		return [];
	}
	let riderTrips;
	riderTrips = await getRiderTrips(driverTrip);
	riderTrips = await getRiderTripSimilarity(driverTrip, riderTrips);
	return riderTrips;
}

/*
 * Handles all trips
 * Gets the directions object from Google and returns it
 */
function tripHandler(trip, callback) {
	let startPoint = trip.origin;
	let endPoint = trip.destination;
	let req = {
		origin: startPoint,
		destination: endPoint
	};
	getDirections(req, function(err, res) {
		callback(res.json);
	});
}

module.exports = {
	driverTripHandler,
	tripHandler,
	cutTripsByBearing,
	cutTripsByDistance,
	cutTripsByTime,
	modifyTrip,
	getInterestSimilarity,
	getRiderTripSimilarity,
	getRiderTrips
};
