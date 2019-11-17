const express = require("express");
const router = express.Router();

// Controllers
// const sendMessage = require("../controllers/sendMessage");
const getMessages = require("../controllers/getMessages");

router.get("/getMessages", getMessages.handleGetMessages);
// router.post("/sendMessage", sendMessage.handleCreateTrip);

module.exports = router;