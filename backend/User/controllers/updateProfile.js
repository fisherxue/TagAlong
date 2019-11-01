const User = require("../models/user");
const mongoose = require("mongoose");
const debug = require("debug")("http");

const createUpdateObject = (req) => {

	const { userID, firstName, lastName, age, gender, email, interests, isDriver, carCapacity, fbToken } = req.body;

	const update = {
		...interests && { interests },
		...firstName && { firstName },
		...lastName && { lastName },
		...age && { age },
		...gender && { gender },
		...email && { email },
		...email && { email },
		...carCapacity && { carCapacity },
		...fbToken && { fbToken }
	};

	update.isDriver = isDriver;

	return update;
};


const handleProfileUpdate = async (req, res) => {

	debug("/profileUpdate hit");
	debug(req.body);
	
	const { userID, firstName, lastName, age, gender, email, interests, isDriver, carCapacity, fbToken } = req.body;

	const update = createUpdateObject(req);

	if (mongoose.Types.ObjectId.isValid(userID)) {

		await User.findByIdAndUpdate(userID, update, { new: true }, (err, user) => {
			if (err) {
				res.status(400).send(err);
			}
			else {
				debug("user updated");
				res.json(user);
			}
		});
	}
	else {
		res.status(400).send("Invalid userID");
	}


	

};




module.exports = {
	handleProfileUpdate
};
