const User = require('../models/user');

const handleProfileUpdate = async (req, res) => {
	
	const { username, age } = req.body;

	const user = await User.findOne({ username });

	if (user) {
		const updatedUser = await User.findOneAndUpdate({username: username}, {
			age: age,
			isDriver: false
		}, {
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
