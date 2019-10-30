const TripStore = require('../models/Trip');
const User = require('../../User/models/user');

const handleDelTrip = async (req, res) => {
	
	const { userID, tripid } = req.body;

	const user = await User.findOne({ username });

	if (mongoose.Types.ObjectId.isValid(userID)) {
		await User.findById(userID, (err, user) => {
			if (err) {
				return res.status(400).send("Unable to find user");
			}
			else {
				TripStore.findByIdAndDelete(tripid, err => {
					if (err) 
						res.status(400).send("trip not found");
					else 
						res.json("trip successfully deleted");
					
				})
			}

		});
	}
	else {
		return res.status(400).send("Invalid userID");
	}



}

module.exports = {
	handleDelTrip
}
