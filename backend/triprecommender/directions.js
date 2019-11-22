/* Google Maps */
// initialize maps client
const googleMapsClient = require("@google/maps").createClient({
	key: "AIzaSyDkjse1zwmX7lw71D5wpKIP0xrbKLG1YIQ"
});

/* Get directions with waypoints */
function getDirectionsWithWaypoints(req, callback) {
	googleMapsClient.directions({
		origin: req.origin,
		destination: req.destination,
		waypoints: req.waypoints,
		optimize: true,
	}, function(err, response) {
		callback(err, response);
	});
}

/* Get directions */
function getDirections(req, callback) {
	googleMapsClient.directions({
		origin: req.origin,
		destination: req.destination,
	}, function(err, response) {
		callback(err, response);
	});
}

module.exports = {
	getDirectionsWithWaypoints,
	getDirections
};