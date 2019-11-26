const User = require("../models/user");
const mongoose = require("mongoose");
const debug = require("debug")("http /logout");


/**
 * handleProfileUpdate: Checks the given userID and updates the user corresponding
 * 						to the userID with the new fields provided.
 */ 

const handleLogout = async (req, res) => {

	debug("/logout hit");
	debug(req.body);
	
	const { userID } = req.body;

	const update = {
		fbToken: ""
	};

	if (!mongoose.Types.ObjectId.isValid(userID)) {
		debug("Invalid userID");
		return res.status(400).send("Invalid userID");
	}

	const updateduser = await User.findByIdAndUpdate(userID, update, {new: true});

	debug("updated user", updateduser);
	res.json(updateduser);

};




module.exports = {
	handleLogout
};
