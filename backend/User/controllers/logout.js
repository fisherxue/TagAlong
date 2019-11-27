const User = require("../models/user");
const mongoose = require("mongoose");
const debug = require("debug")("http /logout");


/**
 * handleLogout: Logs out the given user associated by the userID by
 * 			     setting the associated fbToken to an empty string
 */ 

const handleLogout = async (req, res) => {

	debug("/logout hit");
	debug(req.body);
	
	const { userID } = req.body;

	const update = {
		fbToken: ""
	}

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
