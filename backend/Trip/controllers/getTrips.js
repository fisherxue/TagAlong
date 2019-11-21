const TripStore = require("../models/Trip");
const User = require("../../User/models/user");
const mongoose = require("mongoose");
const debug = require("debug")("http /getTrips");



const handleGetTrips = async (req, res) => {
	
	debug(req.headers);

	const userID = req.headers.userid;

	debug("/getTrips hit");
	debug("get userID", userID);

	if (mongoose.Types.ObjectId.isValid(userID)) {
		await User.findById(userID, (err, user) => {
			if (user) {
				TripStore.find({ userID }, (err, trips) => {
					debug("responded with trips", trips);
					res.json({
						trips: trips
					});
				});
			} else {
				debug("user not found");
				return res.status(400).send("Unable to find user");
			}

		});
	} else {
		debug("invalid userID");
		return res.status(400).send("Invalid userID");
	}




};

module.exports = {
	handleGetTrips
};
