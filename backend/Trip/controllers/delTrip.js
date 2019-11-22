const TripStore = require("../models/Trip");
const User = require("../../User/models/user");
const mongoose = require("mongoose");
const debug = require("debug")("http /delTrip");


const handleDelTrip = async (req, res) => {
	
	const userID = req.body.userID;
	const tripID = req.body.tripID;

	if (!mongoose.Types.ObjectId.isValid(userID) | !mongoose.Types.ObjectId.isValid(tripID)) {
		debug("Invalid userID");
		return res.status(400).send("Invalid userID or tripID");
	}

	const user = await User.findById(userID);

	if (!user) {
		debug("Unable to find user");
		return res.status(400).send("Unable to find user");
	}

	await TripStore.findByIdAndDelete(tripID);

	res.json("trip successfully deleted");
};

module.exports = {
	handleDelTrip
};
