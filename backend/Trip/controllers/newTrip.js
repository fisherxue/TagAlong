const TripStore = require('../models/Trip');
const User = require('../../User/models/user');
const firebase = require('firebase-admin');

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
				tripRecommender.driverTripHandler(trip, async function(riderTrips) {
					console.log("121")
				console.log(riderTrips)
				if (typeof riderTrips === "undefined") {
					console.log("101")
					res.status(300).send("NOTHING");
					console.log("102")
				} else {
					let username
					for(const trip of riderTrips) {
						username = trip.username;
						const user = await User.findOne({ username });
						
						if (user) {
							console.log("tried to send")

							const firebaseToken = user.fb_token;
							if (firebaseToken){
								const payload = {
								    notification: {
								    	title: 'Trip Accepted',
								    	body: 'You have been matched with a driver and other riders for the requested trip',
								    }
								};
							 
								const options = {
									priority: 'high',
									timeToLive: 60 * 60 * 24, // 1 day
								};
							 
							 	console.log(firebaseToken);
							 	firebase.messaging().sendToDevice(firebaseToken, payload, options)
							 	.then(res => {
							 		console.log(res.results);
							 	})
							 	.catch(err => {
							 		console.log(err);
							 	});
							 	res.json("Sent");
							}
							else {
								console.log("failed to send")
							}
							
						}
						else {
							return res.status(400).send("Incorrect email or password");
						}
					}
					res.send(riderTrips)
				}
				})
				
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
