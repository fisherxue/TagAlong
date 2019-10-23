const TripStore = require('../models/Trip');
const User = require('../../User/models/user');

const handleDelTrip = async (req, res) => {
	
	const { username } = req.body;

	const user = await User.findOne({ username });

	if (user) {
		TripStore.deleteOne({_id: tripid}, err => {
			if (err) {
				return res.status(400).send("trip not found");
			}
			else {
				return res.json("trip successfully deleted");
			}
		})
	}
	else {
		return res.status(400).send("Incorrect email or password");
	}

}

module.exports = {
	handleDelTrip: handleDelTrip
}
