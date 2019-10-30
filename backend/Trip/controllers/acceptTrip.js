const TripStore = require("../models/Trip");
const User = require("../../User/models/user");
const firebase = require("firebase-admin");

const handleAcceptTrip = async (req, res) => {
	
	const { username, routedata } = req.body;

	const user = await User.findOne({ username });

	if (user) {

		const firebaseToken = user.fbToken;
		const payload = {
		    notification: {
		    	title: "Trip Accepted",
		    	body: "You have been matched with a driver and other riders for the requested trip",
		    }
		};
	 
		const options = {
			priority: "high",
			timeToLive: 60 * 60 * 24, // 1 day
		};

	 	console.log(firebaseToken);
	 	firebase.messaging().sendToDevice(firebaseToken, payload, options)
		.then(res => {
			console.log(res.results);
		})
	 	.catch(err => {
			console.log(err);
		});
		res.json("Sent");
	}
	else {
		return res.status(400).send("Incorrect email or password");
	}

}

module.exports = {
	handleAcceptTrip
}
