const http = require("http");
const express = require("express");
const bodyParser = require("body-parser");
const app = express();
const mongoose = require("mongoose");
const config = require("config");
const firebase = require("firebase-admin");
const serviceAccount = require("./serviceAcc.json");
const debug = require("debug")("app");


const hostname = "127.0.0.1";
const port = 3000;


firebase.initializeApp({
  credential: firebase.credential.cert(serviceAccount),
  databaseURL: "https://swift-citadel-256401.firebaseio.com"
});


if (!config.get("PrivateKey")) {
    debug("FATAL ERROR: PrivateKey is not defined.");
    process.exit(1);
}

// Connect to MongoDB 
mongoose.connect("mongodb://localhost:27017/TagAlong", {useNewUrlParser: true})
	.then(() => debug("Successfully connected to TagAlong MongoDB"))
	.catch((err) => debug("Error connecting to database", err));

app.use(bodyParser.json());

const users = require("./User/routes/users");
const trips = require("./Trip/routes/triproutes");
const chat = require("./Chat/routes/chatroutes");

app.use("/users", users);
app.use("/trips", trips);
app.use("/chat", chat);


app.listen(port, () =>
  debug(`Example app listening on port ${port}!`),
);


