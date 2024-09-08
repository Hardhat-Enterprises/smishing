package Services;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AccountLockService {
    private static final long LOCK_DURATION = 15 * 60 * 1000; // 15 minutes in milliseconds

    // ScheduledExecutorService for periodic tasks
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void startUnlockingService() {
        scheduler.scheduleWithFixedDelay(this::unlockAccounts, 0, LOCK_DURATION, TimeUnit.MILLISECONDS);
    }

    private void unlockAccounts() {
        try {
            List<User> users = getAllLockedUsers(); // Fetch locked users from the database
            long currentTime = System.currentTimeMillis();
            for (User user : users) {
                if (currentTime - user.getLastFailedAttempt() > LOCK_DURATION) {
                    user.setLocked(false);
                    user.setFailedAttempts(0);
                    updateUserInDatabase(user); // Persist changes in the database
                }
            }
        } catch (Exception e) {
            // Log the exception and handle it as necessary
            e.printStackTrace();
        }
    }

    private List<User> getAllLockedUsers() {
        // Replace with actual database retrieval logic
        // This method should return a list of users whose accounts are locked
        return null;
    }

    private void updateUserInDatabase(User user) {
        // Replace with actual database update logic
    }

    public void stopService() {
        scheduler.shutdownNow();
    }
}
