const http = require('http');
const express = require('express');
const bodyParser = require('body-parser');
const knex = require('knex');
const app = express();

//const login = require('./controllers/login);


const hostname = '127.0.0.1';
const port = 3000;


app.use(bodyParser.json());


app.get('/', (req, res) => {
  return res.send('Received a GET HTTP method');
});
app.post('/', (req, res) => {
  return res.send('Received a POST HTTP method');
});
app.put('/', (req, res) => {
  return res.send('Received a PUT HTTP method');
});
app.delete('/', (req, res) => {
  return res.send('Received a DELETE HTTP method');
});

/*
app.post('/login', login.handleSignin(db, bcrypt));
*/


app.listen(port, () =>
  console.log(`Example app listening on port ${port}!`),
);


