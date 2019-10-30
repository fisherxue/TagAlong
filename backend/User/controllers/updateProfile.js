const User = require('../models/user');
const mongoose = require('mongoose');

const handleProfileUpdate = async (req, res) => {
	console.log('/profileUpdate hit');
	console.log(req.body);
	
	const { userID, firstName, lastName, age, gender, email, interests, isDriver, fbToken } = req.body;

	let update = {
	}

	if (age) {
		update.age = age;
	}

	if (gender) {
		update.gender = gender;
	}

	if (email) {
		update.email = email;
	}

	if (interests) {
		update.interests = interests;
	}

	if (isDriver != null) {
		update.isDriver = isDriver;
	}

	if (fbToken) {
		update.fbToken = fbToken;
	}

	if (mongoose.Types.ObjectId.isValid(userID)) {
		console.log("VALID");
		await User.findByIdAndUpdate(userID, update, { new: true }, (err, user) => {
			if (err) {
				return res.status(400).send(err);
			}
			else {
				console.log("user updated");
				res.json(user);
			}
		})
	}
	else {
		return res.status(400).send("Invalid userID");
	}

	

}

module.exports = {
	handleProfileUpdate: handleProfileUpdate
}
