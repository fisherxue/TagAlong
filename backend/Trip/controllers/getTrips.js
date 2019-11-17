const TripStore = require("../models/Trip");
const User = require("../../User/models/user");
const mongoose = require("mongoose");
const debug = require("debug")("http /getTrips");



const handleGetTrips = async (req, res) => {
	
	const userID = req.body;

	debug("/getTrips hit");
	debug("get userID", userID);

	if (mongoose.Types.ObjectId.isValid(userID)) {
		await User.findById(userID, (err, user) => {
			if (err) {
				debug("user not found");
				return res.status(400).send("Unable to find user");
			} else {
				TripStore.find({ userID }, (err, trips) => {
					if (err) {
						debug("trip not found");
						res.status(400).send("trip not found");
					} else {
						debug("responded with trips");
						res.json(trips);
					}
				});
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
