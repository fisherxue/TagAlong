const express = require("express");
const router = express.Router();

// Controllers
const createTrip = require("../controllers/newTrip");
const delTrip = require("../controllers/delTrip");
const getTrips = require("../controllers/getTrips");

router.get("/myTrips", getTrips.handleGetTrips);
router.post("/newTrip", createTrip.handleCreateTrip);
router.delete("/delTrip", delTrip.handleDelTrip);


module.exports = router;