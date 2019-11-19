const User = require("../../User/models/user");
const Chat = require("../models/Chat");
const mongoose = require("mongoose");
const debug = require("debug")("http /newRoom");

const handleNewRoom = async (req, res) => {
	debug("/newRoom hit");
	debug(req.body);

	const users = req.body.users;

	if (users) {
		chat = new Chat({
			users,
			messages: []
		});

		await chat.save();
		res.send(chat);

	}
	else {
		res.status(400).send("No users supplied");
	}

	

}

module.exports = {
	handleNewRoom
};
