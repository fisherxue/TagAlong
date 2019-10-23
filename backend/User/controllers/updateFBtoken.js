const User = require('../models/user');

const handleFBtokenUpdate = async (req, res) => {
	
	const { username, fb_token} = req.body;

	const user = await User.findOne({ username });

	if (user) {
		const updatedUser = await User.findOneAndUpdate({username: username}, {fb_token: fb_token}, {
			new: true
		});

		res.json(updatedUser);
	}
	else {
		return res.status(400).send("Incorrect email or password");
	}

}

module.exports = {
	handleFBtokenUpdate: handleFBtokenUpdate
}
