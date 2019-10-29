const mongoose = require('mongoose');
const ObjectId = mongoose.Schema.Types.ObjectId;

const TripSchema = mongoose.Schema({
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
	user_id: {
		type: ObjectId,
		required: true
	},
	isFulfilled: {
		type: Boolean,
		required: true
	}
	

});


const Trip = module.exports = mongoose.model('TripStore', TripSchema);



