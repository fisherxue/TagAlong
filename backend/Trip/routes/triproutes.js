const express = require("express");
const router = express.Router();

// Controllers
const createTrip = require("../controllers/newTrip");
const delTrip = require("../controllers/delTrip");
const getTrips = require("../controllers/getTrips");
const acceptTrip = require("../controllers/acceptTrip");
const getRecommendedTrips = require("../controllers/getRecommendedTrips");

router.get("/myTrips", getTrips.handleGetTrips);
router.post("/newTrip", createTrip.handleCreateTrip);
router.delete("/delTrip", delTrip.handleDelTrip);
router.post("/acceptTrip", acceptTrip.handleAcceptTrip);
router.get("/getRecommendedTrips", getRecommendedTrips.handleGetRecommendedTrips)
module.exports = router;