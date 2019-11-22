const TripStore = require("../models/Trip");
const User = require("../../User/models/user");
const mongoose = require("mongoose");
const debug = require("debug")("http /delTrip");
const firebase = require("firebase-admin");


const sendChatNotif = async (user, message) => {
	const firebaseToken = user.fbToken;

	if (typeof firebaseToken != 'undefined'){
		const payload = {
			notification: {
				title: "New Message",
				body: message
			}
		};
	
		const options = {
			priority: "high",
			timeToLive: 60 * 60 * 24, // 1 day
		};

		firebase.messaging().sendToDevice(firebaseToken, payload, options);
		// .then((res) => {
		// 	debug(res.results);
		// })
		// .catch((err) => {
		// 	debug(err);
		// });
	} else {
		debug("invalid firebaseToken");
	}
};


const handleDelTrip = async (req, res) => {
	
	const userID = req.body.userID;
	const tripID = req.body.tripID;

	if (!mongoose.Types.ObjectId.isValid(userID) | !mongoose.Types.ObjectId.isValid(tripID)) {
		debug("Invalid userID or tripID");
		return res.status(400).send("Invalid userID or tripID");
	}

	const user = await User.findById(userID);

	if (!user) {
		debug("Unable to find user");
		return res.status(400).send("Unable to find user");
	}

	const trip = await TripStore.findByIdAndDelete(tripID);

	const parenttrip = await TripStore.findByIdAndDelete(trip.driverTripID);

	const index = parenttrip.taggedUsers.indexOf(user.username);
	if (index > -1) {
	  parenttrip.taggedUsers.splice(index, 1);
	}

	const tripindex = parenttrip.taggedTrips.indexOf(tripID);
	if (tripindex > -1) {
		parenttrip.taggedTrips.splice(tripindex, 1);
	}
	

	await parenttrip.save();

	const driveruser = await TripStore.findById(parenttrip.userID);

	sendChatNotif(driveruser, `User ${user.username} has left your trip`);

	res.send({
		status: 'OK',
		message: 'trip successfully deleted'
	})
};

module.exports = {
	handleDelTrip
};
