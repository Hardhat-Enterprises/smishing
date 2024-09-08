package Services;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

class AccountUnLockService {
    private static final long LOCK_DURATION = 15 * 60 * 1000; // 15 minutes in milliseconds
    private static final Logger logger = Logger.getLogger(String.valueOf(AccountUnLockService.class));
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1); // ScheduledExecutorService for periodic tasks
    public void startUnlockingService() {
        scheduler.scheduleWithFixedDelay(this::unlockAccounts, 0, LOCK_DURATION, TimeUnit.MILLISECONDS);
    }

    private void unlockAccounts() {
        try {
            List<User> users = getAllLockedUsers(); // Fetch locked users from the database
            long currentTime = System.currentTimeMillis();
            for (User user : users) {
                if (user.getLastFailedAttempt() != null) {
                    long lastFailedAttemptTime = user.getLastFailedAttempt().getTime(); // Convert Date to milliseconds for comparison
                    if (currentTime - lastFailedAttemptTime > LOCK_DURATION) {
                        user.setLocked(false);
                        user.setFailedAttempts(0);
                        updateUserInDatabase(user);
                    }
                }
            }
        } catch (Exception e) {
            // Log the exception and handle it as necessary
            e.printStackTrace();
        }
    }


    private void stopService() {
        scheduler.shutdownNow();
    }

    private List<User> getAllLockedUsers() {
        return new ArrayList<>();
    }

    private void updateUserInDatabase(User user) {
    }
}


