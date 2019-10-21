const isLoggedIn = async (req, res, next) => {
	
	if (req.user) {
		next();
	}
	else {
		res.status(500).json({error:'current user is not authenticated'});
	}

}

module.exports = {
	isLoggedIn: isLoggedIn
}
