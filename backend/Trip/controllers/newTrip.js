const TripStore = require('../models/Trip');
const User = require('../../User/models/user');

const tripScheduler = require('../../router/routeScheduler');
const directions = require('../../router/directions');

const handleCreateTrip = async (req, res) => {
		
	console.log("/newTrip hit");

	console.log(req.body);

	const { username, arrivaltime, tripRoute, isDriverTrip } = req.body;

	const user = await User.findOne({ username });

	if (user) {

		let bigboijson;

		await directions.getDirections(tripRoute, res => {
			bigboijson = res;
		})

		trip = new TripStore({
			username: username,
			arrivaltime: arrivaltime,
			tripRoute: bigboijson,
			isDriverTrip: isDriverTrip
		})

		// await trip.save(err => {
		// 	if(err)
		// 		res.status(400).send("ERROR MISSING FIELD")
		// 	else
		// 		res.send(trip);
		// });

		if (isDriverTrip) {
			await tripScheduler.routeSchedulerRequestHandler(trip.tripRoute, (res, usertrips) => {

				users = [];
				usertrips.forEach(usertrip => {
					users.push(usertrip.username);
				})

				optimizedTrip = new TripStore({
					username: username,
					arrivaltime: arrivaltime,
					tripRoute: res,
					isDriverTrip: true,
					ass_user: users
				})

				optimizedTrip.save(err => {
					if(err)
						res.status(400).send("ERROR MISSING FIELD");
					else
						res.send(trip);
				})



			})
		}
		else {
			trip.save(err => {
					if(err)
						res.status(400).send("ERROR MISSING FIELD");
					else
						res.send(trip);
				})
		}

	}
	else {
		return res.status(400).send("Incorrect email or password");
	}

}

module.exports = {
	handleCreateTrip: handleCreateTrip
}
