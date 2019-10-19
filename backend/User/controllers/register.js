const User = require('../models/user');
const bcrypt = require('bcryptjs');

const handleRegister = async (req, res) => {

	const { username, firstName, lastName, email, joinedDate, password } = req.body;

	let user = await User.findOne({ email });

	if (user) {
		return res.status(400).send('User already exists.');
	}
	else {
		user = new User({
			username: username,
			firstName: firstName,
			lastName: lastName,
			email: email,
			joinedDate: new Date(),
			password: bcrypt.hashSync(password, 10)

		});
		await user.save()
			.then(user => {
				res.send(user);
			})
			.catch( err => {
				res.status(400).send(err);
			});
	}
}

module.exports = {
	handleRegister: handleRegister
}
