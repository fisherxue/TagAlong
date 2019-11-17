const TripStore = require("../models/Trip");
const User = require("../../User/models/user");
const firebase = require("firebase-admin");
const mongoose = require("mongoose");
const debug = require("debug")("http");



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
	
		debug(firebaseToken);
		firebase.messaging().sendToDevice(firebaseToken, payload, options)
		.then((res) => {
			debug(res.results);
		})
		.catch((err) => {
			debug(err);
		});
	}
	else {
		debug("failed to send");
	}
};

const notifyAllRiders = async (riderTrips, callback) => {
	for(const trip of riderTrips) {
		let username = trip.username;
		debug(username, "USERNAME");
		const user = await User.findOne({ username });

		debug(user, "SADFASDFASDF");

		if (user) {
			debug("tried to send", user);
			await sendNotif(user);
		}
		else {
			callback(Error("Unable to find user"));
		}

	}
};


const handleCreateTrip = async (req, res) => {
		
	debug("/newTrip hit");

	const { userID, username, arrivalTime, tripRoute, isDriverTrip } = req.body;

	debug(req.body);

	if (mongoose.Types.ObjectId.isValid(userID)) {
		await User.findById(userID, (err, user) => {
			if (err) {
				return res.status(400).send("Unable to find user");
			}
			else {

				debug("creating new trip");

				let trip = new TripStore({
					username,
					userID,
					arrivalTime,
					tripRoute: tripRoute,
					isDriverTrip,
					isFulfilled: false
				});

				debug(trip);

				tripRecommender.tripHandler(tripRoute.nameValuePairs, function(resp) {
					trip.tripRoute = resp.json;


					trip.save((err) => {
						debug(err);

					});


					if (isDriverTrip) {
						debug("DRIVER TRIP");
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
						debug("NOT A DRIVER TRIP");
						res.send(trip);
					}
				});
			}
		});
	} else {
		return res.status(400).send("Invalid userID");
	}

};

module.exports = {
	handleCreateTrip
};
