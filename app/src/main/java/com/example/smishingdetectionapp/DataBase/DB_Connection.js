const express = require('express');
const mongoose = require('mongoose');
const morgan = require('morgan');
const details = require('./Collection');
const bcrypt = require('bcryptjs');
const app = express();

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

// Sign-Up Endpoint
app.post('/signup', async (req, res) => {

    const { FullName, PhoneNumber, Email, Password, VerificationCode } = req.body;

        const hashedPassword = await bcrypt.hash(Password, 10);

        const user = new details({
            FullName,
            PhoneNumber,
            Email,
            Password: hashedPassword,
            verificationCode: VerificationCode,
            isVerified: false
        });

        await user.save();

        res.status(201).json({ message: 'Registration successful! Please check your email for verification.' });

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



// Login endpoint
app.post('/login', async (req, res) => {
    try {
        const { email, password } = req.body;

        // Find user by email
        const user = await details.findOne({ Email: email });
        if (!user) {
            return res.status(404).json({ message: 'User not found' });
        }

        if (!user.isVerified) {
            return res.status(403).json({ message: 'Please verify your email before logging in' });
        }

        // Compare the password with the hashed password stored in the database
        const isMatch = await bcrypt.compare(password, user.Password);
        if (isMatch) {
            res.status(200).json({ message: 'Login successful' });
        } else {
            res.status(401).json({ message: 'Wrong credentials' });
        }
    } catch (err) {
        console.error("Error during login:", err);
        res.status(500).json({ message: 'Server error' });
    }
});

// Express.js route to verify the user's email
app.post('/verify', async (req, res) => {
    try {
        const { email, verificationCode } = req.body;

        // Find the user by email and verification code
        const user = await User.findOne({ email, verificationCode });

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

