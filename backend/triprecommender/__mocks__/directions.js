const fs = require("fs");

const directions = jest.genMockFromModule("../directions.js");

function getDirections(req, callback) {
    if (req.origin === undefined || req.destination === undefined) {
        throw new Error("Error");
    } 
    fs.readFile("./triprecommender/__mocks__/getDirections.json", "utf8", (err, data) => {
        callback(err, JSON.parse(data));
    });
}

function getDirectionsWithWaypoints(req, callback) {
    if (req.origin === undefined || req.destination === undefined) {
        throw new Error("Error");
    } 
    fs.readFile("./triprecommender/__mocks__/getDirections.json", "utf8", (err, data) => {
        callback(err, JSON.parse(data));
    });
}

directions.getDirections = getDirections;
directions.getDirectionsWithWaypoints = getDirectionsWithWaypoints;

module.exports = directions;