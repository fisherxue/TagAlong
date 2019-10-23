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
	ass_user: [{
		type: String,
		required: true
	}],
	username: {
		type: String,
		required:true
	},
	isDriverTrip: {
		type: Boolean,
		required: true
	}
	

});


const Trip = module.exports = mongoose.model('TripStore', TripSchema);



