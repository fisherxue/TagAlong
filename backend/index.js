const http = require('http');
const express = require('express');
const bodyParser = require('body-parser');
const app = express();
const mongoose = require('mongoose');
const config = require('config');

const hostname = '127.0.0.1';
const port = 3000;



if (!config.secret) {
    console.error('FATAL ERROR: secret is not defined.');
    process.exit(1);
}

// Connect to MongoDB 
mongoose.connect('mongodb://localhost:27017/TagAlong', {useNewUrlParser: true})
	.then(() => console.log("Successfully connected to TagAlong MongoDB"))
	.catch(err => console.log("Error connecting to database", err));

app.use(bodyParser.json());

const users = require('./User/routes/users');

app.use('/users', users);


app.listen(port, () =>
  console.log(`Example app listening on port ${port}!`),
);


