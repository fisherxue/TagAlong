const TripStore = require("../models/Trip");
const User = require("../../User/models/user");
const firebase = require("firebase-admin");
const mongoose = require("mongoose");


const tripRecommender = require("../../triprecommender/recommender");

const sendNotif = async (user) => {
	const firebaseToken = user.fbToken;
	if (firebaseToken){
		const payload = {
			notification: {
				title: "Trip Accepted",
				body: "You have been matched with a driver and other riders for the requested trip",
			}
		};
	
		const options = {
			priority: "high",
			timeToLive: 60 * 60 * 24, // 1 day
		};
	
		console.log(firebaseToken);
		firebase.messaging().sendToDevice(firebaseToken, payload, options)
		.then((res) => {
			console.log(res.results);
		})
		.catch((err) => {
			console.log(err);
		});
	}
	else {
		console.log("failed to send");
	}
};

const notifyAllRiders = async (riderTrips, callback) => {
	for(const trip of riderTrips) {
		let username = trip.username;
		console.log(username, "USERNAME");
		const user = await User.findOne({ username });

		console.log(user, "SADFASDFASDF");

		if (user) {
			console.log("tried to send", user);
			await sendNotif(user);
		}
		else {
			callback(Error("Unable to find user"));
		}

	}
};


const handleCreateTrip = async (req, res) => {
		
	console.log("/newTrip hit");

	const { userID, username, arrivalTime, tripRoute, isDriverTrip } = req.body;

	console.log(req.body);

	if (mongoose.Types.ObjectId.isValid(userID)) {
		await User.findById(userID, (err, user) => {
			if (err) {
				return res.status(400).send("Unable to find user");
			}
			else {

				console.log("creating new trip");

				let trip = new TripStore({
					username,
					userID,
					arrivalTime,
					tripRoute: JSON.stringify(tripRoute),
					isDriverTrip,
					isFulfilled: false
				});

				console.log(trip);

				tripRecommender.tripHandler(tripRoute.nameValuePairs, function(resp) {
					trip.tripRoute = JSON.stringify(resp.json);


					trip.save((err) => {
						console.log(err);
						
					});


					if (isDriverTrip) {
						console.log("DRIVER TRIP");
						tripRecommender.driverTripHandler(trip, async function(riderTrips, driverTrip) {
						if (typeof riderTrips === "undefined") {
							res.status(300).send("NOTHING");
						} else {
							notifyAllRiders(riderTrips, (err) => {
								if (err) {
									res.status(400).json("unable to find user");
								}
								else {
									res.send("Sent succesfully");
								}
							});
							res.send(driverTrip);
						}
						});
						
					} 
					else {
						console.log("NOT A DRIVER TRIP");
						res.send(trip);
					}
				});
			}
		});
	}
	else {
		return res.status(400).send("Invalid userID");
	}

};

module.exports = {
	handleCreateTrip
};
