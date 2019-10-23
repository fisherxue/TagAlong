const express = require('express');
const router = express.Router();

// User Model
const TripStore = require('../models/Trip');

// Controllers
const createTrip = require('../controllers/newTrip');
const delTrip = require('../controllers/delTrip');
const acceptTrip = require('../controllers/acceptTrip')

// router.get('/getTrip', createTrip.handleCreateTrip);
// router.post('/newTrip', createTrip.handleCreateTrip);
// router.put('/updateTrip', )
router.delete('/delTrip', delTrip.handleDelTrip);
router.post('/acceptTrip', acceptTrip.handleAcceptTrip);


module.exports = router;