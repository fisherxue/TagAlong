const User = require("../../User/models/user");
const Chat = require("../models/Chat");
const mongoose = require("mongoose");
const debug = require("debug")("http /sendMessages");
const firebase = require("firebase-admin");


const sendChatNotif = async (user, message) => {
	const firebaseToken = user.fbToken;
	if (firebaseToken){
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
	}
	else {
		debug("invalid firebaseToken");
	}
};

const notifyUsers = async (users, message) => {
	for(const username of users) {
		const user = await User.findOne({ username });
		if (user) {
			await sendChatNotif(user, message);
		}
		else {
			debug("user not found");
		}
	}
};

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
	await notifyUsers(chat.users, message);


};

module.exports = {
	handleSendMessages
};
