const TripStore = require("../models/Trip");
const User = require("../../User/models/user");
const firebase = require("firebase-admin");
const debug = require("debug")("http");


const handleAcceptTrip = async (req, res) => {

	debug("/acceptTrip hit")
	
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

		debug(firebaseToken);
		firebase.messaging().sendToDevice(firebaseToken, payload, options)
		.then((res) => {
			debug(res.results);
		})
		.catch((err) => {
			debug(err);
		});
		res.json("Sent");
	}
	else {
		return res.status(400).send("Incorrect email or password");
	}

};

module.exports = {
	handleAcceptTrip
};
