const express = require("express");
const router = express.Router();

// Controllers
const sendMessage = require("../controllers/sendMessage");
const getMessages = require("../controllers/getMessages");
const newRoom = require("../controllers/newRoom");


router.post("/getMessages", getMessages.handleGetMessages);
router.post("/sendMessage", sendMessage.handleSendMessages);
router.post("/newRoom", newRoom.handleNewRoom);

module.exports = router;