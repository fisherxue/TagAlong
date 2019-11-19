const TripStore = require("../models/Trip");
const User = require("../../User/models/user");
const mongoose = require("mongoose");


const handleDelTrip = async (req, res) => {
	
	const userID = req.body.userID;
	const tripID = req.body.tripID;

	if (mongoose.Types.ObjectId.isValid(userID)) {
		await User.findById(userID, (err, user) => {
			if (!user) {
				return res.status(400).send("Unable to find user");
			} else {
				TripStore.findByIdAndDelete(tripID, (err) => {
					if (err) {
						res.status(400).send("trip not found");
					} else {
						res.json("trip successfully deleted");
					}
				});
			}

		});
	} else {
		return res.status(400).send("Invalid userID");
	}




};

module.exports = {
	handleDelTrip
};
