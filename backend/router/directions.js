const googleMapsKey = require('./keys.js').GoogleDirectionsAPIKey;
// initialize maps client
const googleMapsClient = require('@google/maps').createClient({
    key: googleMapsKey
});
 
const fs = require('fs');

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
        if (err) throw err;
        console.log(response);  
        callback(err, response);
    });
};

// sample usage

var inputs = {
    //origin: '4175 W 29th Ave, Vancouver, BC V6S 1V1',
    //destination: '4125 W 8th Ave, Vancouver, BC V6R 4P9',
    //destination: 'Indigo, 2505 Granville St, Vancouver, BC V6H 3G7',
    destination: 'AMS Student Nest, 6133 University Blvd, Vancouver, BC V6T 1Z1',
    origin: '3939 W 16th Ave, Vancouver, BC V6R 2C9',
    mode: "driving",
}

getDirections(inputs, function(err, response) {
    var json = JSON.stringify(response.json, null, 2);
	fs.writeFile('data.json', json, 'utf8', function(err) {
        if (err) throw err;
    });
});
