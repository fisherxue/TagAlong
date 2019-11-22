const TripStore = require("../models/Trip");
const mongoose = require("mongoose");
const tripRecommender = require("../../triprecommender/recommender");
const debug = require("debug")("http /getRecommendedTrips");
const User = require("../../User/models/user");


const handleGetRecommendedTrips = async (req, res) => {

	debug("/getRecommendedTrips hit");

	const userID = req.headers.userid;

	if (!mongoose.Types.ObjectId.isValid(userID)) {
		debug("invalid userID");
		return res.status(400).send("Invalid userID");
		
	}

	try {
		const user = await User.findById(userID);
		if (!user) {
			return res.status(400).send("User not found with corresponding userID");
		}
		if (user.isDriver === false) {
			return res.status(400).send("User is not a driver");
		}

		const trips = await TripStore.find({ userID });
		if (!trips) {
			return res.status(400).send("Driver has no trips");
		}
		let recommendedTrips = [];

		for (const trip of trips) {
			let appendingobj = {
				drivertrip: {},
				riderTrips: []
			};

			appendingobj.drivertrip = trip;

			const ridertrips = await tripRecommender.driverTripHandler(trip);
			
			appendingobj.riderTrips = ridertrips; 
			debug("current appendending object", appendingobj);
			recommendedTrips.push(appendingobj);
		}
		
		debug("responing recommended trips", recommendedTrips);
		res.json({trips: recommendedTrips});

	} catch (err) {
		debug(err);
	}

};

module.exports = {
	handleGetRecommendedTrips
};
