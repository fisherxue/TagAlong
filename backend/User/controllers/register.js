const User = require("../models/user");
const bcrypt = require("bcryptjs");
const debug = require("debug")("http");


/**
 * handleRegister: Given the username, email password and firebasetoken 
 *				   from the body of request, a new user object is created
 * 				   and stored in the database. If the user is successfully
 * 				   created, the user is sent back as the response. 
 */ 

const handleRegister = async (req, res) => {

	debug("/register hit");
	debug(req.body);

	const { username, email, password, fbToken } = req.body;

	let user = await User.findOne({ email });

	if (user) {
		return res.status(400).send("User already exists.");
	} 
	else {
		if (password) {
			user = new User({
				username,
				email,
				joinedDate: new Date(),
				password: bcrypt.hashSync(password, 10),
				fbToken,
				interests: [2,2,2,2,2],
				age: 0,
				gender: "",
				firstName: "",
				lastName: "",
				carCapacity: 0

			});
			await user.save((err) => {
				if (err) {
					res.status(400).send("ERROR MISSING FIELD");
				} else {
					res.send(user);
				}
			});
		} else {
			res.status(400).send("ERROR MISSING PASSWORD");
		}

		
	}
};

module.exports = {
	handleRegister
};
