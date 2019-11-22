const TripStore = require("../models/Trip");
const User = require("../../User/models/user");
const mongoose = require("mongoose");
const Chat = require("../../Chat/models/Chat");
const tripRecommender = require("../../triprecommender/recommender");
const debug = require("debug")("http /acceptTrip");
const firebase = require("firebase-admin");


const addUsertoChatRoom = (username, roomID) => {
	if(mongoose.Types.ObjectId.isValid(roomID)) {
		Chat.findById(roomID, (err, chat) => {
			if (chat) {
				chat.users.push(username);
				chat.save();
			}
			else {
				debug("chat not found");
			}
		});
	}
	else {
		debug("invalid roomID");
	}
};

const sendNotif = async (user) => {
	const firebaseToken = user.fbToken;
	if (firebaseToken){
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

		firebase.messaging().sendToDevice(firebaseToken, payload, options)
		.catch((err) => {
		});
	}
	else {
		debug("invalid firebaseToken");
	}
};

const handleAcceptTrip = async (req, res) => {
	
	debug("/acceptTrip hit");

	const userID = req.body.userID;
	const drivertripID = req.body.tripID;
	const usertripID = req.body.usertripID;

	debug("userID", userID);
	debug("drivertripID", drivertripID);
	debug("usertripID", usertripID);

	if (!mongoose.Types.ObjectId.isValid(userID) | !mongoose.Types.ObjectId.isValid(drivertripID) | !mongoose.Types.ObjectId.isValid(usertripID)) {
		debug("Invalid user ID or roomID");
		return res.status(400).send("Invalid userID or roomID");
	}

	const user = await User.findById(userID);

	if (!user) {
		debug("Unable to find user");
		return res.status(400).send("Unable to find user");
	}

	const driverTrip = await TripStore.findById(drivertripID);

	if (!driverTrip) {
		debug("driver trip not found");
		return res.status(400).send("driver trip not found");
	}

	const riderTrip = await TripStore.findById(usertripID);

	if (!riderTrip) {
		debug("rider trip not found");
		return res.status(400).send("rider trip not found");
	}

	const rider_user = await User.findById(riderTrip.userID);

	if (!rider_user) {
		debug("rider user not found");
		return res.status(400).send("rider user not found");
	} 

	debug("selected driver trip", driverTrip);

	tripRecommender.modifyTrip(driverTrip, [riderTrip], triproute => {
		driverTrip.tripRoute = triproute;
		driverTrip.taggedUsers.push(riderTrip.username);
		driverTrip.taggedTrips.push(riderTrip._id);

		debug(driverTrip, "driverTrip update");
		
		driverTrip.save();
		riderTrip.driverTripID = driverTrip._id;
		riderTrip.chatroomID = driverTrip.chatroomID;
		riderTrip.isFulfilled = true;
		riderTrip.taggedUsers.push(driverTrip.username);
		riderTrip.save();

		addUsertoChatRoom(rider_user.username, driverTrip.chatroomID);
		debug("added ", rider_user.username, "to chatroom", driverTrip.chatroomID);

		res.send({
			status: 'OK',
			message: 'user successfully added to trip'
		});
	});

	

};

module.exports = {
	handleAcceptTrip
};
