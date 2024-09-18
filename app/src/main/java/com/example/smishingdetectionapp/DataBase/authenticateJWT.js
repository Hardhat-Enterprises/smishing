const jwt = require('jsonwebtoken');
const User = require('./Collection.js');
const JWT_SECRET = 'your_jwt_secret';

// Middleware to verify JWT
const authenticateJWT = (req, res, next) => {
    const token = req.headers.authorization?.split(' ')[1];  // Extract token from Bearer header

    if (!token) {
        return res.status(401).json({ message: 'Unauthorized' });
    }

    try {
        const decoded = jwt.verify(token, JWT_SECRET);  // Verify the token
        req.userId = decoded.userId;  // Attach userId (MongoDB _id) to request object
        next();
    } catch (err) {
        return res.status(401).json({ message: 'Invalid token' });
    }
};

// Example of a protected route
app.get('/protected-route', authenticateJWT, async (req, res) => {
    try {
        // Find the user by MongoDB _id (from the token)
        const user = await User.findById(req.userId);
        if (!user) {
            return res.status(404).json({ message: 'User not found' });
        }

        res.json({ message: 'Access granted', user });
    } catch (err) {
        res.status(500).json({ message: 'Server error' });
    }
});
