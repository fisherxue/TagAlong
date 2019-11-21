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
		})
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
	
	const userID = req.body.userID;
	const drivertripID = req.body.tripID;
	const usertripID = req.body.usertripID;

	if (mongoose.Types.ObjectId.isValid(userID)) {
		User.findById(userID, (err, user) => {
			if (!user) {
				return res.status(400).send("Unable to find user");
			} else {
				TripStore.findById(drivertripID, (err, driverTrip) => {
					if (err) {
						res.status(400).send("trip not found");
					} else {
						// Update drivertrip
						TripStore.findById(usertripID, (err, ridertrip) => {
							if (ridertrip) {
								driverTrip.tripRoute = tripRecommender.modifyTrip(driverTrip, riderTrip);
								driverTrip.taggedUsers.push(ridertrip.username);
								driverTrip.taggedTrips.push(ridertrip._id);

								debug(driverTrip, "driverTrip update");
								TripStore.findByIdAndUpdate(driverTrip._id, driverTrip, {new: true}, (err) => {
									if (err) {
										debug(err);
									}
								});

								// Add user to chatroom
								addUsertoChatRoom(usertripID.username, driverTrip.chatroomID);
								debug("added ", usertripID.username, "to chatroom", "driverTrip.chatroomID");
							}
							else {
								debug("ridertrip not found");
								res.status(400).send("ridertrip not found");
							}
						});
					}
				});
			}
		});
	} else {
		return res.status(400).send("Invalid userID");
	}

};

module.exports = {
	handleAcceptTrip
};
