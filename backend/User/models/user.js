const mongoose = require("mongoose");

const UserSchema = mongoose.Schema({
	username: {
		type: String, 
		unique: true, 
		required: true
	},
	firstName: {
		type: String, 
		required: false
	},
	lastName: {
		type: String, 
		required: false
	},
	age: {
		type: Number,
		required: false
	},
	gender: {
		type: String,
		required: false
	},
	email: { 
		type: String, 
		required: true
	},
	joinedDate: {
		type: Date, 
		default: Date.now
	},
	password: { 
		type: String, 
		required: true 
	},
	interests: {
		type: Array,
		required: false
	},
	isDriver: {
		type: Boolean,
		required: false
	},
	carCapacity: {
		type: Number,
		required: false
	},
	fbToken: {
		type: String,
		required: false
	}

});

module.exports = mongoose.model("User", UserSchema);



