const mongoose = require('mongoose');
const Schema = mongoose.Schema;
const bcrypt = require('bcryptjs');

const userSchema = new Schema({
    FullName: {
        type: String,
        required: true
    },
    PhoneNumber: {
        type: String,
        required: true,
        unique: true
    },
    Email: {
        type: String,
        required: true,
        unique: true
    },
    Password: {
        type: String,
        required: true
    },


});

// Hashes the Passwords
userSchema.pre('save', async function(next) {
    if (!this.isModified('Password')) {
        return next();
    }
    this.Password = await bcrypt.hash(this.Password, 10);
    next();
});


// Method to compare the provided password with the hashed password in the database
userSchema.methods.comparePassword = async function(candidatePassword) {
    return await bcrypt.compare(candidatePassword, this.Password);
};

const details = mongoose.model('details', userSchema);
module.exports = details;
