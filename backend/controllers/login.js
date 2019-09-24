const handleLogin = (db, bcrypt) => (req, res) => {
	db.select('username', 'hash').from('login')
		.where('username', '=', req.body.username)
		.then(data => {
			console.log(data);
			const isValid = bcrypt.compareSync(req.body.password, data[0].hash);
			if (isValid) {
				return db.select('*').from('users')
					.where('username', '=', req.body.username)
					.then(user => {
					 	res.json(user[0])
					})
					.catch(err => res.status(400).json('unable to get user'))
			} else {
				res.status(400).json('wrong credentials');
			}
		})
		.catch(err => res.status(400).json('wrong credentials'));

}

module.exports = {
	handleLogin: handleLogin
}
