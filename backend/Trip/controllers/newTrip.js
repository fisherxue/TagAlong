const TripStore = require("../models/Trip");
const User = require("../../User/models/user");
const firebase = require("firebase-admin");
const mongoose = require("mongoose");
const debug = require("debug")("http");
const tripRecommender = require("../../triprecommender/recommender");
const Chat = require("../../Chat/models/Chat");


const createNewRoom = (username) => {
	const users = [username];
	chat = new Chat({
		users,
		messages: []
	});

	chat.save((chatroom) => {
		return chatroom._id;
	})
}

const handleCreateTrip = async (req, res) => {
		
	debug("/newTrip hit");

	const { userID, username, arrivalTime, tripRoute, isDriverTrip } = req.body;

	debug(req.body);

	if (mongoose.Types.ObjectId.isValid(userID)) {
		User.findById(userID, (err, user) => {
			if (!user) {
				res.status(400).send("Unable to find user");
			}
			else {

				debug("creating new trip");

				let trip = new TripStore({
					username,
					taggedUsers: [],
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
						debug("Trip is a DRIVER TRIP");
						tripRecommender.driverTripHandler(trip, function(riderTrips, driverTrip) {
						if (typeof riderTrips === "undefined") {
							return res.status(300).send("NOTHING");
						} else {

							// Create Chat room
							driverTrip.chatroomID = createNewRoom(username);

							// Create new Rider trip;

							riderTrips = riderTrips.slice(0, 4); // should slice by driver car size
							
							riderTrips.forEach((ridertrip) => {
								driverTrip.recommendedTrips.push(ridertrip);
							})

							driverTrip.isFulfilled = true;
							TripStore.findByIdAndUpdate(driverTrip._id, driverTrip, {new: true}, (err) => {
								if (err) {
									debug(err);
								}
							})
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
		res.status(400).send("Invalid userID");
	}

};

module.exports = {
	handleCreateTrip
};
