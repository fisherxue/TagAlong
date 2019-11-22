const express = require("express");
const router = express.Router();

// User Model
const User = require("../models/user");

// Controllers
const register = require("../controllers/register");
const login =  require("../controllers/login");
const updateProfile = require("../controllers/updateProfile");
const getProfile = require("../controllers/getProfile");

router.post("/register", register.handleRegister);
router.post("/login", login.handleLogin);
router.put("/updateProfile", updateProfile.handleProfileUpdate);
router.get("/getProfile", getProfile.handleGetProfile);

module.exports = router;