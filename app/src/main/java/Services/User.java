package Services;

import java.util.Date;
import java.util.List;

public class User {
    private boolean locked;
    private int failedAttempts;
    private Date lastFailedAttempt;
    private String email;

    public User(String username, String hashedPassword) {
    }

    public boolean isLocked() { return locked; }
    public void setLocked(boolean locked) { this.locked = locked; }
    public int getFailedAttempts() { return failedAttempts; }
    public void setFailedAttempts(int failedAttempts) { this.failedAttempts = failedAttempts; }
    public Date getLastFailedAttempt() { return lastFailedAttempt; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Date getLockoutStart() {
        return null;
    }

    public void setLockoutStart(Object o) {
    }

    public Object getPassword() {
        return null;
    }
}
