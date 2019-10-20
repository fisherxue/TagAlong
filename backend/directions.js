
// initialize maps client
const googleMapsClient = require('@google/maps').createClient({
    key: 'AIzaSyB3bmJtSMJbdacuIwj6W--i35gUoDTv0f8'
});

/* 
 * TODO: figure out what datetime object it takes
 * Abstraction in case we have to change underlying API
 */ 
function getDirections(req, callback) {
    googleMapsClient.directions({
    origin: req.origin,
    destination: req.destination,
    mode: req.mode,
    }, function(err, response) {
    callback(err, response);
    });
};

// sample input
var inputs = {
    origin: 'Indigo, 2505 Granville St, Vancouver, BC V6H 3G7',
    destination: 'AMS Student Nest, 6133 University Blvd, Vancouver, BC V6T 1Z1',
    mode: "driving",
}

