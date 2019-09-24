const handleRegister = (db, bcrypt) => (req, res) => {
	const { email, username, password, name } = req.body;
	const saltRounds = 10;
	const hash = bcrypt.hashSync(password, saltRounds);
	db.transaction(trx => {
		trx.insert({
			hash: hash,
			username: username
		})
		.into('login')
		.returning('username')
		.then(username => {
			return trx('users')
				.returning('*')
				.insert({
					username: username[0],
					email: email,
					name: name,
					joined: new Date()

				})
				.then(user => {
					res.json(user[0]);
				})
		})
		.then(trx.commit)
		.catch(trx.rollback)
	})
	.catch(err => {
		console.log(err);
		res.status(400).json('User already exists.')
	});
}

module.exports = {
	handleRegister: handleRegister
}
