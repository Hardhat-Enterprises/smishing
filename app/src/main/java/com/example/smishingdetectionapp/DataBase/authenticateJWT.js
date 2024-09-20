const jwt = require('jsonwebtoken');

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

