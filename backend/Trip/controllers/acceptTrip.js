const TripStore = require("../models/Trip");
const User = require("../../User/models/user");
const mongoose = require("mongoose");
const Chat = require("../../Chat/models/Chat");
const tripRecommender = require("../../triprecommender/recommender");
const debug = require("debug")("http /acceptTrip");
const firebase = require("firebase-admin");


/**
 * addUsertoCharRoom: adds the given username to the given chatroom
 */

const addUsertoChatRoom = (username, roomID) => {
	Chat.findById(roomID, (err, chat) => {
		if (chat) {
			chat.users.push(username);
			chat.save();
		}
		else {
			debug("chat not found");
		}
	});
};

/**
 * sendNotif: sends a notification to the given user
 */

const sendNotif = async (user) => {
	const firebaseToken = user.fbToken;
	if (firebaseToken){
		const payload = {
			notification: {
				title: "Trip Accepted",
				body: "You have been matched with a driver and other riders for the requested trip",
			}, 
			data: {
				type: "Trip"
			}
		};
	
		const options = {
			priority: "high",
			timeToLive: 60 * 60 * 24, // 1 day
		};

		firebase.messaging().sendToDevice(firebaseToken, payload, options);
	}
	else {
		debug("invalid firebaseToken");
	}
};

/**
 * handleAcceptTrip: checks if the given userID, drivertripID and usertripID if its valid,
 *					 if the ids are valid, the user and the trips are then looked up in the
 *					 database, if any are not found, the function responds with an error.
 *					 The function then modifies the trip to fit in the new user trip and adds
 * 					 the new user to the taggedtrips and to the chatroom of the trip
 */

const handleAcceptTrip = async (req, res) => {
	
	debug("/acceptTrip hit");

	const userID = req.body.userID;
	const drivertripID = req.body.tripID;
	const usertripID = req.body.usertripID;

	debug("userID", userID);
	debug("drivertripID", drivertripID);
	debug("usertripID", usertripID);

	if (!mongoose.Types.ObjectId.isValid(userID) | !mongoose.Types.ObjectId.isValid(drivertripID) | !mongoose.Types.ObjectId.isValid(usertripID)) {
		debug("Invalid user ID or drivertripID or usertripID");
		return res.status(400).send("Invalid user ID or drivertripID or usertripID");
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

	const riderUser = await User.findById(riderTrip.userID);

	if (!riderUser) {
		debug("rider user not found");
		return res.status(400).send("rider user not found");
	} 

	debug("selected driver trip", driverTrip);

	tripRecommender.modifyTrip(driverTrip, [riderTrip], async (triproute) => {
		driverTrip.tripRoute = triproute;
		driverTrip.taggedUsers.push(riderTrip.username);
		driverTrip.taggedTrips.push(riderTrip._id);

		debug(driverTrip, "driverTrip update");
		
		await driverTrip.save();
		riderTrip.driverTripID = driverTrip._id;
		riderTrip.chatroomID = driverTrip.chatroomID;
		riderTrip.isFulfilled = true;
		riderTrip.taggedUsers.push(driverTrip.username);
		await riderTrip.save();

		sendNotif(riderUser);

		addUsertoChatRoom(riderUser.username, driverTrip.chatroomID);
		debug("added ", riderUser.username, "to chatroom", driverTrip.chatroomID);

		res.send({
			status: "OK",
			message: "user successfully added to trip"
		});
	});

	

};

module.exports = {
	handleAcceptTrip
};
