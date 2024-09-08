package Services;
import java.util.Date;
import java.util.Objects;

public class AuthenticationService {
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCK_DURATION = 15 * 60 * 1000; // 15 minutes

    public boolean authenticate(String username, String password) {
        User User = getUserFromDatabase(username);
        if (user == null) {
            return false; // User not found
        }

        if (isAccountLocked(user)) {
            return false; // Account is locked
        }

        if (passwordMatches(user, password)) {
            user.setFailedAttempts(0); // Reset failed attempts on successful login
            user.setLockoutStart(null); // Clear lockout start time
            updateUserInDatabase(user);
            return true; // Authentication successful
        } else {
            handleFailedLogin(user); // Handle the failed login attempt
            return false; // Authentication failed
        }
    }

    private boolean isAccountLocked(User user) {
        if (user.getFailedAttempts() >= MAX_FAILED_ATTEMPTS) {
            if (user.getLockoutStart() == null) {
                return false; // No lockout start time, so account is not locked
            }

            long lockoutTime = user.getLockoutStart().getTime();
            long currentTime = new Date().getTime();
            if (currentTime - lockoutTime < LOCK_DURATION) {
                return true; // Account is still locked
            } else {
                user.setFailedAttempts(0); // Reset failed attempts after lockout duration
                user.setLockoutStart(null); // Clear lockout start time
                updateUserInDatabase(user);
                return false; // Account is no longer locked
            }
        }
        return false; // Account is not locked
    }

    private boolean passwordMatches(User user, String password) {
        // Replace with secure password hashing and comparison
        return Objects.equals(user.getPassword(), password);
    }

    private void handleFailedLogin(User user) {
        int failedAttempts = user.getFailedAttempts();
        failedAttempts++;
        user.setFailedAttempts(failedAttempts);
        if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
            user.setLockoutStart(new Date()); // Set lockout start time
            sendLockoutNotification(user); // Notify user about the lockout
        }
        updateUserInDatabase(user);
    }

    private User getUserFromDatabase(String username) {
        // Replace with actual database retrieval logic
        return new User(username, "hashedPassword"); // Replace with actual logic
    }

    private void updateUserInDatabase(User user) {
        // Replace with actual database update logic
    }

    private void sendLockoutNotification(User user) {
        // Use EmailService to send the lockout notification
        String userEmail = user.getEmail();
        EmailService emailService = new EmailServiceImpl();
        emailService.sendLockoutNotification(userEmail);
    }
}
