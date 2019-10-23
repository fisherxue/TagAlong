const mongoose = require('mongoose');
const ObjectId = mongoose.Schema.Types.ObjectId;

const TripSchema = mongoose.Schema({
	arrivaltime: {
		type: Date,
		required: true
	},
	triproute: {
		type: String,
		required: true
	},
	ass_user: [{
		type: String,
		required: true
	}]
	

});


const Trip = module.exports = mongoose.model('TripStore', TripSchema);



