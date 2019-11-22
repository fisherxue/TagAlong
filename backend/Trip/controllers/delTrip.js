const TripStore = require("../models/Trip");
const User = require("../../User/models/user");
const mongoose = require("mongoose");
const debug = require("debug")("http /delTrip");
const firebase = require("firebase-admin");


const sendChatNotif = async (user, message) => {
	const firebaseToken = user.fbToken;

	if (typeof firebaseToken != "undefined"){
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
	
	const userID = req.headers.userid;
	const tripID = req.headers.tripid;

	debug("userID: ", userID);
	debug("tripID: ", tripID);

	if (!mongoose.Types.ObjectId.isValid(userID) | !mongoose.Types.ObjectId.isValid(tripID)) {
		debug("Invalid userID or tripID");
		return res.status(400).send("Invalid userID or tripID");
	}

	const user = await User.findById(userID);

	if (!user) {
		debug("Unable to find user");
		return res.status(400).send("Unable to find user");
	}

	const trip = await TripStore.findById(tripID);

	if (!trip) {
		debug("trip not found");
		return res.status(400).send("trip not found");
	}

	if (trip.isDriverTrip) {
		if (trip.taggedTrips) {
			for (const ridertrip of trip.taggedTrips) {
				const foundridertrip = await TripStore.findByIdAndUpdate(ridertrip._id, {
					isFulfilled: false,
					taggedUsers: [],
					driverTripID: undefined,
					chatroomID: undefined
				}, {new: true});
				const rideruser = await User.findById(foundridertrip.userID);
				sendChatNotif(rideruser, `Driver ${user.username} has deleted his trip`);
			}
		}
	} else {
		if (trip.isFulfilled) {
			const parenttrip = await TripStore.findById(trip.driverTripID);

			if (!parenttrip) {
				debug("Unable to find driver trip");
				return res.status(400).send("Unable to find driver trip");
			}

			debug("parenttrip found: ", parenttrip);

			const driveruser = await User.findById(parenttrip.userID);

			if (!driveruser) {
				debug("Unable to find driver");
				return res.status(400).send("Unable to find driver");
			}

			const index = parenttrip.taggedUsers.indexOf(user.username);
			if (index > -1) {
				parenttrip.taggedUsers.splice(index, 1);
			}

			const tripindex = parenttrip.taggedTrips.indexOf(tripID);
			if (tripindex > -1) {
				parenttrip.taggedTrips.splice(tripindex, 1);
			}
			
			await parenttrip.save();

			sendChatNotif(driveruser, `User ${user.username} has left your trip`);
		}
	}

	await TripStore.findByIdAndDelete(tripID);

	debug("deleted trip: ", trip);

	

	res.send({
		status: "OK",
		message: "trip successfully deleted"
	});
};

module.exports = {
	handleDelTrip
};
