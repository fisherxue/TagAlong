const TripStore = require("../models/Trip");
const User = require("../../User/models/user");
const mongoose = require("mongoose");


const handleDelTrip = async (req, res) => {
	
	const userID = req.body.userID;
	const tripID = req.body.tripID;

	if (!mongoose.Types.ObjectId.isValid(userID)) {
		debug("Invalid userID");
		return res.status(400).send("Invalid userID");
	}

	const user = await User.findById(userID);

	if (!user) {
		debug("Unable to find user");
		return res.status(400).send("Unable to find user");
	}

	await TripStore.findByIdAndDelete(tripID);
};

module.exports = {
	handleDelTrip
};
