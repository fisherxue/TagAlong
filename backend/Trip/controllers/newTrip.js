const TripStore = require("../models/Trip");
const User = require("../../User/models/user");
const mongoose = require("mongoose");
const debug = require("debug")("http /newTrip");
const tripRecommender = require("../../triprecommender/recommender");
const Chat = require("../../Chat/models/Chat");


const createNewRoom = async (username) => {
	const users = [username];
	const chat = new Chat({
		users,
		messages: []
	});

	chat.save((err, chatroom) => {
		debug("chat id", chatroom);
		return chatroom._id;
	})
};

const handleCreateTrip = async (req, res) => {
		
	debug("/newTrip hit");

	const { userID, username, arrivalTime, tripRoute, isDriverTrip } = req.body;

	debug(req.body);

	if (!mongoose.Types.ObjectId.isValid(userID)) {
		debug("Invalid userID");
		return res.status(400).send("Invalid userID");
	}

	const user = await User.findById(userID);

	if (!user) {
		debug("Unable to find user");
		return res.status(400).send("Unable to find user");
	}

	let trip = new TripStore({
		username,
		taggedUsers: [],
		userID,
		arrivalTime,
		tripRoute,
		isDriverTrip,
		isFulfilled: false
	});

	tripRecommender.tripHandler(tripRoute.nameValuePairs, async (resp) => {
		trip.tripRoute = resp.json;

		if (isDriverTrip) {
			debug("Trip is a DRIVER TRIP");
			trip.chatroomID = await createNewRoom(username);
			debug("created chatroom, id: ", trip.chatroomID);
			trip.isFulfilled = true;
		}

		trip.save();
		res.send(trip);

	});

};

module.exports = {
	handleCreateTrip
};
