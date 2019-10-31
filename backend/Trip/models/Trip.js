const mongoose = require("mongoose");
const ObjectId = mongoose.Schema.Types.ObjectId;

const tripschema = mongoose.Schema({
	arrivaltime: {
		type: Date,
		required: true
	},
	tripRoute: {
		type: String,
		required: true
	},
	taggedUsers: [{
		type: String,
		required: true
	}],
	isDriverTrip: {
		type: Boolean,
		required: true
	},
	userID: {
		type: ObjectId,
		required: true
	},
	isFulfilled: {
		type: Boolean,
		required: true
	},
	username: {
		type: String,
		required: true
	}
	

});


const Trip = module.exports = mongoose.model("TripStore", tripschema);



