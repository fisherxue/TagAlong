const User = require('../models/user');
const bcrypt = require('bcryptjs');

const handleRegister = async (req, res) => {

	console.log("/register hit");

	const { username, firstName, lastName, age, gender, email, interests, isDriver, password } = req.body;

	let user = await User.findOne({ email });

	if (user) {
		return res.status(400).send('User already exists.');
	}
	else {
		user = new User({
			username: username,
			firstName: firstName,
			lastName: lastName,
			age: age,
			gender: gender,
			email: email,
			joinedDate: new Date(),
			password: bcrypt.hashSync(password, 10),
			isDriver: isDriver

		});
		await user.save(err => {
			if(err)
				res.status(400).send("ERROR MISSING FIELD")
			else
				res.send(user);
		})
	}
}

module.exports = {
	handleRegister: handleRegister
}