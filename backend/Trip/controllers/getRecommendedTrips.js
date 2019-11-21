const TripStore = require("../models/Trip");
const mongoose = require("mongoose");
const tripRecommender = require("../../triprecommender/recommender");
const debug = require("debug")("http /getRecommendedTrips");
const User = require("../../User/models/user");


const handleGetRecommendedTrips = async (req, res) => {

	debug("/getRecommendedTrips hit");

	const userID = req.headers.userid;

	if (mongoose.Types.ObjectId.isValid(userID)) {
		User.findById(userID, (err, user) => {
			if (user) {
				if (user.isDriver) {
					TripStore.find({ userID }, (err, trips) => {
						let recommendedTrips = [];
						trips.forEach(trip => {
							let appendingobj = {
								drivertrip: {}
								riderTrips: []
							};

							appendingobj.drivertrip = trip;
							tripRecommender.driverTripHandler(trip, function(riderTrips, driverTrip) {
								appendingobj.riderTrips = riderTrips;
							});
						})

						debug("responing recommended trips", appendingobj);
						res.json(appendingobj);
					})
				} else {
					res.status(400).send("User is not a driver");
				}

				
			} else {
				res.status(400).send("Unable to find user");
			}
		});
	} else {
		debug("invalid userID");
		res.status(400).send("Invalid userID");
	}

};

module.exports = {
	handleGetRecommendedTrips
};
