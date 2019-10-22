const mongoose = require('mongoose');
const ObjectId = mongoose.Schema.Types.ObjectId;

const TripSchema = mongoose.Schema({
	user_id: {
		type: ObjectId, 
		unique: true, 
		required: true
	},
	arrivaltime: {
		type: Date,
		required: true
	},
	triproute: {
		type: String,
		required: true
	},
	trip_ass: [{
		type: String
	}]
	

});


const Trip = module.exports = mongoose.model('TripStore', TripSchema);



