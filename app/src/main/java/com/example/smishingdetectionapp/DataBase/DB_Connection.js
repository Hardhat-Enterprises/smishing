const express = require('express');
const mongoose = require('mongoose');
const morgan = require('morgan');
const details = require('./Collection');
const bcrypt = require('bcryptjs');
const session = require('express-session');
const jwt = require('jsonwebtoken');
const authenticateJWT = require('./authenticateJWT');






const app = express();





// Protected route
app.get('/protected-route', authenticateJWT, async (req, res) => {
    try {
        // Find the user by MongoDB _id (from the token)
        const user = await details.findById(req.userId);
        if (!user) {
            return res.status(404).json({ message: 'User not found' });
        }

        res.json({ message: 'Access granted', user });
    } catch (err) {
        res.status(500).json({ message: 'Server error' });
    }
});







const URI = "mongodb+srv://Admin:DB_Access123@userlogin.pqphm5b.mongodb.net/User_DB?retryWrites=true&w=majority&appName=UserLogin";

mongoose.connect(URI)
    .then((result) => {
        app.listen(3000, () => console.log("Server is running on port 3000"));
        console.log("Connected to MongoDB");
    })
    .catch((err) => console.log("MongoDB connection error:", err));



app.set('view engine', 'ejs');
app.use(express.static('public'));
app.use(morgan('dev'));
app.use(express.json());





const JWT_SECRET = 'your_jwt_secret';  // Use a strong, secure secret



// Sign-Up Endpoint
app.post('/signup', async (req, res) => {
    const { FullName, PhoneNumber, Email, Password, VerificationCode } = req.body;

    try {
        const hashedPassword = await bcrypt.hash(Password, 10);




        const user = new details({

            FullName,
            PhoneNumber,
            Email,
            Password: hashedPassword,  // Use the hashed password here
            verificationCode: VerificationCode,
        });

        await user.save();

        res.status(201).json({ message: 'Registration successful! Please check your email for verification.' });

    } catch (err) {
        console.error(err);
        res.status(500).json({ message: 'Error during registration.' });
    }
});


// Login Endpoint
app.post('/login', async (req, res) => {
    const { email, password } = req.body;

    try {
        // Find the user by email
        const user = await details.findOne({ Email: email });


        if (!user) {
            return res.status(401).json({ message: 'Invalid email or password' });
        }

        // Compare the password
        const isMatch = await user.comparePassword(password);

        if (!isMatch) {
            return res.status(401).json({ message: 'Invalid email or password' });
        }

        // Create a JWT containing the user's _id
        const token = jwt.sign(
            { userId: user._id },  // Payload with userId
            JWT_SECRET,
            { expiresIn: '1h' }  // Token expires in 1 hour
        );

        // Respond with the JWT token and user details
        res.status(200).json({
            message: 'Login successful!',
            token,  // Include JWT token in response
            userId: user._id,
            name: user.FullName,
            email: user.Email
        });

    } catch (err) {
        res.status(500).json({ message: 'Server error' });
    }
});



// Middleware to verify JWT
const verifyToken = (req, res, next) => {
    const token = req.headers.authorization?.split(' ')[1]; // Get the token from the Authorization header

    if (!token) {
        return res.status(401).json({ message: 'No token provided' });
    }

    try {
        const decoded = jwt.verify(token, JWT_SECRET);
        req.user = decoded; // Attach the decoded token (user data) to the request
        next();
    } catch (error) {
        return res.status(401).json({ message: 'Invalid token' });
    }
};





// Route to get the logged-in user's details
app.get('/user', async (req, res) => {
    const token = req.headers.authorization?.split(' ')[1];  // Get the token from Authorization header

    if (!token) {
        return res.status(401).json({ message: 'No token provided' });
    }

    try {
        // Verify the token and get the decoded user ID
        const decoded = jwt.verify(token, JWT_SECRET);
        const userId = decoded.userId;

        // Find the user in MongoDB using the user ID
        const user = await details.findById(userId);

        if (!user) {
            return res.status(404).json({ message: 'User not found' });
        }

        // Send back the user's email
        res.json({ email: user.email });
    } catch (error) {
        res.status(401).json({ message: 'Invalid token' });
    }
});

// Email Check Endpoint
app.post('/checkemail', async (req, res) => {
    try {
        const { email } = req.body;

        // Check if the email exists in the database
        const existingUser = await details.findOne({ Email: email });
        if (existingUser) {
            return res.status(409).json({ message: 'Email already exists' });
        } else {
            return res.status(200).json({ message: 'Email is available' });
        }
    } catch (err) {
        console.error("Error checking email:", err);
        res.status(500).json({ message: 'Server error' });
    }
});




// Express.js route to verify the user's email
app.post('/verify', async (req, res) => {
const user = await details.findOne({ email, verificationCode });

    try {
        const { email, verificationCode } = req.body;

        // Find the user by email and verification code
        const user = await details.findOne({ email, verificationCode });

        if (!user) {
            return res.status(400).json({ message: 'Invalid or expired verification code.' });
        }

        // Mark the user as verified
        user.isVerified = true;
        user.verificationCode = null; // Clear the verification code
        await user.save();

        res.status(200).json({ message: 'Email verified successfully!' });
    } catch (err) {
        res.status(500).json({ message: 'Server error' });
    }
});



