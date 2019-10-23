const TripStore = require('../models/Trip');
const User = require('../../User/models/user');

const tripRecommender = require('../../triprecommender/recommender');

const handleCreateTrip = async (req, res) => {
		
	console.log("/newTrip hit");

	console.log(req.body);

	const { username, arrivaltime, tripRoute, isDriverTrip } = req.body;

	const user = await User.findOne({ username });

	if (user) {

		trip = new TripStore({
			username: username,
			arrivaltime: arrivaltime,
			tripRoute: JSON.stringify(tripRoute),
			isDriverTrip: isDriverTrip
		})

		console.log(trip, "17")

		tripRecommender.tripHandler(tripRoute.nameValuePairs, function(resp) {
			console.log(typeof resp, "12")
			trip.tripRoute = JSON.stringify(resp.json)
			console.log(resp.json)

			trip.save(err => {
				console.log(err);
			});

			console.log("77")

			if (isDriverTrip) {
				const riderTrips = tripRecommender.driverTripHandler(trip)
				console.log("121")
				console.log(riderTrips)
				if (typeof riderTrips === "undefined") {
					console.log("101")
					res.status(300).send("NOTHING");
					console.log("102")
				} else {
					console.log("202")
					res.send(riderTrips)
				}
			} else {
				res.send(trip)
			}
		})
	} else {
		res.status(400).send("USER NOT FOUND")
	}

}

module.exports = {
	handleCreateTrip: handleCreateTrip
}
