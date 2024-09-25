const fs = require('fs');
const https = require('https');
const express = require('express');
const app = express();

const options = {
    key: fs.readFileSync('C:/Program Files/OpenSSL-Win64/bin/privatekey.pem'),
    cert: fs.readFileSync('C:/Program Files/OpenSSL-Win64/bin/certificate.pem')
};

app.get('/', (req, res) => {
    res.send('MongoDB Connection Successful');
});

// HTTPS server
https.createServer(options, app).listen(3000, () => {
    console.log('Server is running on https://192.168.0.12:3000');
}).on('error', (err) => {
    console.error('Error starting server:', err);
});
