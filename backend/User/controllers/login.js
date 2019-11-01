const jwt = require("jsonwebtoken");
const User = require("../models/user");
const bcrypt = require("bcryptjs");
const config = require("config");
const debug = require("debug")("http");


const handleLogin = async (req, res) => {

	debug("/login hit")
	
	const { username, password, fbToken } = req.body;

	const user = await User.findOne({ username });

	if (user && bcrypt.compareSync(password, user.password)) {
		user.fbToken = fbToken;
		user.save();

		res.json(user);
	}
	else {
		return res.status(400).send("Incorrect email or password");
	}

};

module.exports = {
	handleLogin
};
