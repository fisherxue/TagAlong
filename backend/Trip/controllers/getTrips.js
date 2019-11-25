const TripStore = require("../models/Trip");
const User = require("../../User/models/user");
const mongoose = require("mongoose");
const debug = require("debug")("http /getTrips");

/**
 * handleCreateTrip: Checks the userID and returns all the trips associated
 * 					 with the userID.
 *
 */

const handleGetTrips = async (req, res) => {
	
	debug(req.headers);

	const userID = req.headers.userid;

	debug("/getTrips hit");
	debug("get userID", userID);

	if (!mongoose.Types.ObjectId.isValid(userID)) {
		debug("invalid userID");
		return res.status(400).send("Invalid userID");
	}

	const user = await User.findById(userID);

	if (!user) {
		debug("user not found");
		return res.status(400).send("Unable to find user");
	}

	const trips = await TripStore.find({ userID });

	if (!trips) {
		debug("no trips found");
		return res.status(400).send("No trips found");
	}

	debug("responded with trips", trips);
	res.json({trips});

};

module.exports = {
	handleGetTrips
};
