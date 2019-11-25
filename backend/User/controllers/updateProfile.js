const User = require("../models/user");
const mongoose = require("mongoose");
const debug = require("debug")("http /updateProfile");


/**
 * createUpdateObject: Creates an update object based on whether the variable
 * 					   is undefined or not. Returns an object with all new variables
 *
 */

const createUpdateObject = (req) => {

	const { userID, firstName, lastName, age, gender, email, interests, isDriver, carCapacity, fbToken } = req.body;

	const update = {
		...interests && { interests },
		...firstName && { firstName },
		...lastName && { lastName },
		...age && { age },
		...gender && { gender },
		...email && { email },
		...carCapacity && { carCapacity },
		...fbToken && { fbToken }
	};

	update.isDriver = isDriver;

	return update;
};

/**
 * handleProfileUpdate: Checks the given userID and updates the user corresponding
 * 						to the userID with the new fields provided.
 */ 

const handleProfileUpdate = async (req, res) => {

	debug("/profileUpdate hit");
	debug(req.body);
	
	const { userID, firstName, lastName, age, gender, email, interests, isDriver, carCapacity, fbToken } = req.body;

	if (!mongoose.Types.ObjectId.isValid(userID)) {
		debug("Invalid userID");
		return res.status(400).send("Invalid userID");
	}

	const update = createUpdateObject(req);

	const updateduser = await User.findByIdAndUpdate(userID, update, {new: true});

	debug("updated user", updateduser);
	res.json(updateduser);

};




module.exports = {
	handleProfileUpdate
};
