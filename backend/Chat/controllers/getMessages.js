const User = require("../../User/models/user");
const Chat = require("../models/Chat");
const mongoose = require("mongoose");
const debug = require("debug")("http /getMessages");

/**
 * handleGetMessages: Checks the given userID and roomID and returns the 
 * 					  appropriate chatroom corresponding to roomID
 */ 


const handleGetMessages = async (req, res) => {
	
	debug("/getMessages hit");
	debug(req.headers);

	const userID = req.headers.userid;
	const roomID = req.headers.roomid;

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

	const chatroom = await Chat.findById(roomID);

	res.json(chatroom);


};

module.exports = {
	handleGetMessages
};
