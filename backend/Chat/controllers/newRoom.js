const User = require("../../User/models/user");
const Chat = require("../models/Chat");
const mongoose = require("mongoose");
const debug = require("debug")("http /newRoom");

const handleNewRoom = async (req, res) => {
	debug("/newRoom hit");
	debug(req.body);

	const users = req.body.users;

	chat = new Chat({
		users,
		messages: []
	});

	await chat.save((err) => {
		if (err) {
			debug(err);
			res.status(400).send("ERROR CREATING NEW ROOM");
		}
		else {
			res.send(chat);
		}
	})
}

module.exports = {
	handleNewRoom
};
