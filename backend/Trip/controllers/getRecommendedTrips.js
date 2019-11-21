const TripStore = require("../models/Trip");
const mongoose = require("mongoose");
const tripRecommender = require("../../triprecommender/recommender");
const debug = require("debug")("http /getRecommendedTrips");


const handleGetRecommendedTrips = async (req, res) => {

	debug("/getRecommendedTrips hit");

	const tripID = req.headers.tripid;

	debug("tripID" , tripID);

	if (mongoose.Types.ObjectId.isValid(tripID)) {
		await TripStore.findById(tripID, (err, trip) => {
			if (trip) {
				tripRecommender.driverTripHandler(trip, function(riderTrips, driverTrip) {
					res.json({ trips: riderTrips });
				});
			} else {
				res.status(400).send("trip not found");
			}

		});
	} else {
		return res.status(400).send("Invalid userID");
	}

};

module.exports = {
	handleGetRecommendedTrips
};
