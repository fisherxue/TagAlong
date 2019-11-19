const http = require("http");
const express = require("express");
const bodyParser = require("body-parser");
const app = express();
const mongoose = require("mongoose");
const firebase = require("firebase-admin");
const serviceAccount = require("./serviceAcc.json");
const debug = require("debug")("app");

firebase.initializeApp({
  credential: firebase.credential.cert(serviceAccount),
  databaseURL: "https://swift-citadel-256401.firebaseio.com"
});

app.use(bodyParser.json());

const users = require("./User/routes/users");
const trips = require("./Trip/routes/triproutes");
const chat = require("./Chat/routes/chatroutes");

app.use("/users", users);
app.use("/trips", trips);
app.use("/chat", chat);

module.exports = app;



