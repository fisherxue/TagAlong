const TripStore = require('../models/Trip');
const User = require('../../User/models/user');

const handleCreateTrip = async (req, res) => {
	
	const { username, routedata } = req.body;

	const user = await User.findOne({ username });

	if (user) {
		trip = new Trip({

		})

		await trip.save(err => {
			if(err)
				res.status(400).send("ERROR MISSING FIELD")
			else
				res.send(trip);
		});
	}
	else {
		return res.status(400).send("Incorrect email or password");
	}

}

module.exports = {
	handleCreateTrip: handleCreateTrip
}
