const mongoose = require("mongoose");
const ObjectId = mongoose.Schema.Types.ObjectId;
const Mixed = mongoose.Schema.Types.Mixed;

const TripSchema = mongoose.Schema({
	username: {
		type: String,
		required: true
	},
	userID: {
		type: ObjectId,
		required: true
	},
	isDriverTrip: {
		type: Boolean,
		required: true
	},
	arrivalTime: {
		type: Date,
		required: true
	},
	tripRoute: {
		type: Mixed,
		required: true
	},
	taggedUsers: [{
		type: String,
		required: true
	}],	
	driverTripID: {
		type: ObjectId
	},
	taggedTrips: [{
		type: ObjectId
	}],
	isFulfilled: {
		type: Boolean,
		required: true
	},
	chatroomID: {
		type: ObjectId
	}

	

});


module.exports = mongoose.model("TripStore", TripSchema);



