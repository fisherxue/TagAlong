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
	
		debug(firebaseToken);
		firebase.messaging().sendToDevice(firebaseToken, payload, options)
		.then((res) => {
			debug(res.results);
		})
		.catch((err) => {
			debug(err);
		});
	}
	else {
		debug("invalid firebaseToken");
	}
}

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
}

const handleSendMessages = async (req, res) => {
	
	debug("/sendMessage hit");
	debug(req.body);

	const { userID, roomID, message } = req.body;

	debug("get userID", userID);

	if (mongoose.Types.ObjectId.isValid(userID)) {
		await User.findById(userID, async (err, user) => {
			if (err) {
				debug("user not found");
				res.status(400).send("Unable to find user");
			} else {
				if (user) {
					const username = user.username;

					if (mongoose.Types.ObjectId.isValid(roomID)) {
						await Chat.findById(roomID, async (err, chat) => {
							if (chat) {
								chat.messages.push({
									username: username,
									message: message
								})
								chat.save(async (err) => {
									if (err) {
										res.status(400).send("failed to send message");
									}
									else {
										res.send(chat);
										await notifyUsers(chat.users, message);
									}
									
								})
							}
							else {
								debug("chat room not found");
								res.status(400).send("chat room not found");
							}
							
						})
					}
					else {
						debug("invalid roomID");
						res.status(400).send("Invalid room ID");
					}

				}
				else {
					res.status(400).send("Unable to find user");
				}
				
			}

		});
	} else {
		debug("invalid userID");
		return res.status(400).send("Invalid userID");
	}




};

module.exports = {
	handleSendMessages
};
