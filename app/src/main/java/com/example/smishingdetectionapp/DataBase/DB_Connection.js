const express = require('express');
const mongoose = require('mongoose');
const morgan = require('morgan');
const details = require('./Collection');

const app = express();

const uri = "mongodb+srv://Admin:DB_Access123@userlogin.pqphm5b.mongodb.net/User_DB?retryWrites=true&w=majority&appName=UserLogin";

mongoose.connect(uri, { useNewUrlParser: true, useUnifiedTopology: true })
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
app.post('/signup', (req, res) => {
    details.findOne({ Email: req.body.Email })
        .then(existingUser => {
            if (existingUser) {
                return res.status(409).json({ message: 'Email already exists' }); // Conflict
            }

            const user = new details({
                FullName: req.body.FullName,
                PhoneNumber: req.body.PhoneNumber,
                Email: req.body.Email,
                Password: req.body.Password
            });

            user.save()
                .then((result) => res.status(201).send(result))
                .catch((err) => {
                    console.error("Error saving user:", err);
                    res.status(500).send(err);
                });
        })
        .catch((err) => {
            console.error("Error finding user:", err);
            res.status(500).send(err);
        });
});

// Login endpoint
app.post('/login', (req, res) => {
    const { email, password } = req.body;

    details.findOne({ Email: email, Password: password })
        .then((user) => {
            if (user) {
                res.status(200).json({ message: 'Login successful' });
            } else {
                res.status(404).json({ message: 'Wrong Credentials' });
            }
        })
        .catch((err) => {
            console.error("Error during login:", err);
            res.status(500).send(err);
        });
});
