const User = require('../models/user');

const handleProfileUpdate = async (req, res) => {
	
	const { username, firstName, lastName, age, gender, email, interests, isDriver } = req.body;

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

	const user = await User.findOne({ username });

	if (user) {
		const updatedUser = await User.findOneAndUpdate({username: username}, update, {
			new: true
		});

		res.json(updatedUser);
	}
	else {
		return res.status(400).send("Incorrect email or password");
	}

}

module.exports = {
	handleProfileUpdate: handleProfileUpdate
}
