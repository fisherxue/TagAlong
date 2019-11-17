const TripStore = require("../models/Trip");
const User = require("../../User/models/user");
const mongoose = require("mongoose");
const debug = require("debug")("http");



const handleGetTrips = async (req, res) => {
	
	const userID = req.body;

	debug("/getTrips hit");

	if (mongoose.Types.ObjectId.isValid(userID)) {
		await User.findById(userID, (err, user) => {
			if (err) {
				return res.status(400).send("Unable to find user");
			} else {
				TripStore.find({ userID }, (err, trips) => {
					if (err) {
						res.status(400).send("trip not found");
					} else {
						res.json(trips);
					}
				});
			}

		});
	} else {
		return res.status(400).send("Invalid userID");
	}




};

module.exports = {
	handleGetTrips
};
