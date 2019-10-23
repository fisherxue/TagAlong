const jwt = require('jsonwebtoken');
const User = require('../models/user');
const bcrypt = require('bcryptjs');
const config = require('config');

const handleLogin = async (req, res) => {

	console.log("/login hit");
	
	const { username, password } = req.body;

	const user = await User.findOne({ username });

	if (user && bcrypt.compareSync(password, user.password)) {
		// res.json({
		// 	token: jwt.sign({
		// 		email: user.email,
		// 		_id: user._id,
		// 		firstName: user.firstName,
		// 		lastName: user.lastName
		// 	}, config.get('PrivateKey'))
		// });
		res.json(user);
	}
	else {
		return res.status(400).send("Incorrect email or password");
	}

}

module.exports = {
	handleLogin: handleLogin
}
