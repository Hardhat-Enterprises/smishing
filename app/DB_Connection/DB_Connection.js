const express = require('express');
const mongoose = require('mongoose');
const app = express();

// Middleware to parse JSON requests
app.use(express.json());

const mongoURI = 'mongodb://localhost:27017/mydatabase';

mongoose.connect(mongoURI, {
    useNewUrlParser: true,
    useUnifiedTopology: true
}).then(() => {
    console.log('Connected to MongoDB');
}).catch((err) => {
    console.error('Error connecting to MongoDB:', err);
});

app.get('/', (req, res) => {
    res.send('MongoDB Connection Successful');
});

// A simple GET route
app.get('/api/hello', (req, res) => {
    res.send('Hello from the API!');
});

// A simple POST route
app.post('/api/data', (req, res) => {
    const data = req.body;
    res.send(`You sent: ${JSON.stringify(data)}`);
});

const port = 3000;
app.listen(port, () => {
    console.log(`Server is running on port ${port}`);
});
