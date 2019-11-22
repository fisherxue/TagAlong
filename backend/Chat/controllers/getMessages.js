const User = require("../../User/models/user");
const Chat = require("../models/Chat");
const mongoose = require("mongoose");
const debug = require("debug")("http /getMessages");

const handleGetMessages = async (req, res) => {
	
	debug("/getMessages hit");
	debug(req.headers);

	const userID = req.headers.userid;

	debug("get userID", userID);

	if (!mongoose.Types.ObjectId.isValid(userID)) {
		debug("invalid userID");
		return res.status(400).send("Invalid userID");
	}

	const user = await User.findById(userID);

	if (!user) {
		debug("No user found");
		return res.status(400).send("Unable to find user");
	}

	const username = user.username;

	const chats = await Chat.find({ users: username });

	res.json(chats);


};

module.exports = {
	handleGetMessages
};
