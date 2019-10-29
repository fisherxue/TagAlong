const express = require('express');
const router = express.Router();

// User Model
const User = require('../models/user');

// Controllers
const register = require('../controllers/register');
const login =  require('../controllers/login');
const updateProfile = require('../controllers/updateProfile');
const updateFBtoken = require('../controllers/updateFBtoken');


router.post('/register', register.handleRegister);
router.post('/login', login.handleLogin);
router.put('/updateProfile', updateProfile.handleProfileUpdate);
router.post('/updateFBtoken', updateFBtoken.handleFBtokenUpdate);


module.exports = router;