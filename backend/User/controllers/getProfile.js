const User = require("../../User/models/user");
const mongoose = require("mongoose");
const debug = require("debug")("http /getProfile");

/**
 * handleGetProfile: returns the given user profile based off the username
 */ 

const handleGetProfile = async (req, res) => {
	debug("/getProfile");
	debug(req.headers);

	const username = req.headers.username;

	const user = await User.findOne({ username });

	if (!user) {
		return res.status(400).send("User not found");
	}

	res.send(user);

};

module.exports = {
	handleGetProfile
};