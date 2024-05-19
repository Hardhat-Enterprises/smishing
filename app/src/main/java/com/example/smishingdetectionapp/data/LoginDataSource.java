package com.example.smishingdetectionapp.data;

import com.example.smishingdetectionapp.data.model.LoggedInUser;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {

        /** Temporary arrays to store usernames to test login authentication. Will be updated to work with the User.Json File */
        String[] usernames = {"user1", "user2", "user3"};
        String[] passwords = {"password1", "password2", "password3"};

        try {
            for(int i=0;i<usernames.length;i++)
            {
                if(usernames[i]==username);
                boolean correctusername = true;
            }
            return new Result.Success<>(username);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}
