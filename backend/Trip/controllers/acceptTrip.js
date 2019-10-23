const TripStore = require('../models/Trip');
const User = require('../../User/models/user');
const firebase = require('firebase-admin');

const handleAcceptTrip = async (req, res) => {
	
	const { username, routedata } = req.body;

	const user = await User.findOne({ username });

	if (user) {

		const firebaseToken = String(user._id);
		const payload = {
		    notification: {
		    	title: 'Notification Title',
		    	body: 'This is an example notification',
		    }
		};
	 
		const options = {
			priority: 'high',
			timeToLive: 60 * 60 * 24, // 1 day
		};
	 
	 	firebase.messaging().sendToDevice(firebaseToken, payload, options);
	}
	else {
		return res.status(400).send("Incorrect email or password");
	}

}

module.exports = {
	handleAcceptTrip: handleAcceptTrip
}
