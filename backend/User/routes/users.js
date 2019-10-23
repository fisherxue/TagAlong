const express = require('express');
const router = express.Router();

// User Model
const User = require('../models/user');

// Controllers
const register = require('../controllers/register');
const login =  require('../controllers/login');
const updateProfile = require('../controllers/updateProfile');
const updateFBtoken = require('../controllers/updateFBtoken');


const validateUser = async (req, res, next) => {
	let user;
	try {
		const { username } = req.body;
		user = await User.findOne({ username });
		if (user == null) {
			return res.status(404).json("User not found");
		}
	}
	catch (err) {
		return res.status(500).json({err});
	}

	res.user = user;
	next();
}


router.post('/register', register.handleRegister);
router.post('/login', login.handleLogin);
router.put('/updateProfile', updateProfile.handleProfileUpdate);
router.post('/updateFBtoken', updateFBtoken.handleFBtokenUpdate);


module.exports = router;