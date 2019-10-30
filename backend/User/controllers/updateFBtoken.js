const User = require("../models/user");
const mongoose = require("mongoose");

const handleFBtokenUpdate = async (req, res) => {

	console.log("/updateFBtoken hit");
	
	const { userID, fbToken } = req.body;

	if (mongoose.Types.ObjectId.isValid(userID)) {
		await User.findByIdAndUpdate(userID, { fbToken: fbToken }, {new: true}, (err, user) => {
			if (err) {
				return res.status(400).send("Unable to find user");
			}
			else {
				console.log("user updated");
				res.json(user);
			}

		});
	}
	else 
		return res.status(400).send("Invalid userID");
	

	


}

module.exports = {
	handleFBtokenUpdate
}
