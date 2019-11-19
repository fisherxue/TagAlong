const User = require("../../User/models/user");
const Chat = require("../models/Chat");
const mongoose = require("mongoose");
const debug = require("debug")("http /getMessages");

const handleGetMessages = async (req, res) => {
	
	debug("/getMessages hit");
	debug(req.body);

	const { userID, roomID } = req.body;

	debug("get userID", userID);

	if (mongoose.Types.ObjectId.isValid(userID)) {
		await User.findById(userID, (err, user) => {
			if (user) {
				if (mongoose.Types.ObjectId.isValid(roomID)) {
					Chat.findById(roomID, (err, chat) => {
						res.json(chat);
					});
				} else {
					res.status(400).send("Invalid roomID");
				}
				
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
