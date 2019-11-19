const app = require('./index.js')
const debug = require("debug")("server");
const mongoose = require("mongoose");

const hostname = "127.0.0.1";
const port = 3000;


// Connect to MongoDB 
mongoose.connect("mongodb://localhost:27017/TagAlong", {useNewUrlParser: true, useUnifiedTopology: true})
	.then(() => debug("Successfully connected to TagAlong MongoDB"))
	.catch((err) => debug("Error connecting to database", err));


app.listen(port, () =>
  debug(`Server listening on port ${port}!`),
);