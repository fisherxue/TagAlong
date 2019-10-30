const User = require("../models/user");
const mongoose = require("mongoose");

const handleProfileUpdate = async (req, res) => {
<<<<<<< HEAD
	console.log('/profileUpdate hit');
=======
	console.log("/profileUpdate hit");
	console.log(req.body);
>>>>>>> 4d44fb2b319d77d259d511838f05e0e1bd685373
	
	const { userID, firstName, lastName, age, gender, email, interests, isDriver, carCapacity, fbToken } = req.body;

	let update = {
	}

	if (firstName) {
		update.firstName = firstName;
	}

	if (lastName) {
		update.lastName = lastName;
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

	if (carCapacity) {
		update.carCapacity = carCapacity;
	}

	if (fbToken) {
		update.fbToken = fbToken;
	}

	if (mongoose.Types.ObjectId.isValid(userID)) {

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
	handleProfileUpdate
}
