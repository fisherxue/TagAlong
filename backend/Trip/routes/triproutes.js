const express = require("express");
const router = express.Router();

// Controllers
const createTrip = require("../controllers/newTrip");
const delTrip = require("../controllers/delTrip");
const acceptTrip = require("../controllers/acceptTrip");
const getTrips = require("../controllers/getTrips");

router.post("/myTrips", getTrips.handleGetTrips);
router.post("/newTrip", createTrip.handleCreateTrip);
router.delete("/delTrip", delTrip.handleDelTrip);
router.post("/acceptTrip", acceptTrip.handleAcceptTrip);


module.exports = router;