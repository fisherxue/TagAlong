const mongoose = require("mongoose");

const ChatSchema = mongoose.Schema({

	users: [{
		type: String
	}],
	messages: [{
		username: {
			type: String,
			required: true
		},
		message: {
			type: String
		}
	}]
	

});


module.exports = mongoose.model("Chat", ChatSchema);



