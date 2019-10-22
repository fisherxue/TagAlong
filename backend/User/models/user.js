const mongoose = require('mongoose');

const UserSchema = mongoose.Schema({
	username: {
		type: String, 
		unique: true, 
		required: true
	},
	firstName: {
		type: String, 
		required: true
	},
	lastName: {
		type: String, 
		required: true
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
		required: false
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
		required: true
	}

});


const User = module.exports = mongoose.model('User', UserSchema);



