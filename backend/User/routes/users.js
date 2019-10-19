const express = require('express');
const router = express.Router();

// User Model
const User = require('../models/user');

// Controllers
const register = require('../controllers/register');
const login =  require('../controllers/login');

router.post('/register', register.handleRegister);
router.get('/login', login.handleLogin);


module.exports = router;