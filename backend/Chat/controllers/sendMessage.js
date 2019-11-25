const User = require("../../User/models/user");
const Chat = require("../models/Chat");
const mongoose = require("mongoose");
const debug = require("debug")("http /sendMessages");
const firebase = require("firebase-admin");


/**
 * sendChatNotif: Sends a push notification to the given user containing
 * 				  the message
 */

const sendChatNotif = async (user, message, sentby, roomID) => {
	const firebaseToken = user.fbToken;

	if (typeof firebaseToken != "undefined"){
		const payload = {
			notification: {
				title: "New Message",
				body: sentby + ": " + message;
			},
			data :{
				type: "Chat",
				roomID: roomID
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

/**
 * notifyUsers: Calls sendChatNotif to all users in the chatroom
 * 				to send a push notification to them.   
 */

const notifyUsers = async (users, message, sentby, roomID) => {

	users.forEach(async (username) => {
		if (username != sentby) {
			debug("sending message to: ", username);
			const user = await User.findOne({ username });
			if (user) {
				sendChatNotif(user, message, sentby, roomID);
			}
			else {
				debug("user not found");
			}
		}
	});
};

/**
 * handleSendMessages: Checks the given userID and roomID and sends the message 
 * 					   to the appropriate chatroom corresponding to roomID and 
 * 					   notifies all the users in the room through a push notification
 */ 

const handleSendMessages = async (req, res) => {
	
	debug("/sendMessage hit");
	debug(req.body);

	const { userID, roomID, message } = req.body;

	debug("get userID", userID);

	if (!mongoose.Types.ObjectId.isValid(userID) | !mongoose.Types.ObjectId.isValid(roomID)) {
		debug("invalid userID or roomID");
		return res.status(400).send("Invalid userID or roomID");
	}

	const user = await User.findById(userID);

	if (!user) {
		debug("No user found");
		return res.status(400).send("Unable to find user");
	}

	const chat = await Chat.findById(roomID);

	if (!chat) {
		debug("chat room not found");
		return res.status(400).send("chat room not found");
	}

	const username = user.username;

	chat.messages.push({
		username,
		message
	});
	await chat.save();
	res.send(chat);
	notifyUsers(chat.users, message, username, roomID);


};

module.exports = {
	handleSendMessages
};
