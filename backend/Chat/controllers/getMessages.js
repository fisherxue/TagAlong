const User = require("../../User/models/user");
const Chat = require("../models/Chat");
const mongoose = require("mongoose");
const debug = require("debug")("http /getMessages");

const handleGetMessages = async (req, res) => {
	
	debug("/getMessages hit");
	debug(req.headers);

	const userID = req.headers.userid;

	debug("get userID", userID);

	if (mongoose.Types.ObjectId.isValid(userID)) {
		User.findById(userID, (err, user) => {
			if (user) {
				const username = user.username;
				debug(username);
				Chat.find({users: username}, (err, chats) => {
					debug(chats);
					if (chats) {
						res.json(chats);
					}
					else {
						res.status(400).send("No chatrooms");
					}
				})
				
			} else {
				res.status(400).send("Unable to find user");
			}
		});
	} else {
		debug("invalid userID");
		res.status(400).send("Invalid userID");
	}


};

module.exports = {
	handleGetMessages
};
