package com.example.smishingdetectionapp;

import android.content.Context;
import com.example.smishingdetectionapp.detections.DatabaseAccess;

public class DatabaseThreatRepository implements ThreatRepository {

    private static final int SAFE = 0;     // LOW
    private static final int CAUTION = 1;  // MEDIUM
    private static final int ALERT = 2;    // HIGH

    // Your requested bands (by COUNT, not percent):
    // 0..14 -> LOW, 15..59 -> MEDIUM, >=60 -> HIGH
    private static final int CAUTION_MIN_COUNT = 15;
    private static final int ALERT_MIN_COUNT   = 60;

    private final Context context;

    public DatabaseThreatRepository(Context context) {
        this.context = context;
    }

    /** Legacy name: now returns the raw detection COUNT from DB. */
    @Override
    public int calculateThreatScore() {
        DatabaseAccess db = DatabaseAccess.getInstance(context);
        db.open();
        try {
            return db.getCounter(); // total detections
        } finally {
            db.close();
        }
    }

    /** 0 = LOW, 1 = MEDIUM, 2 = HIGH based only on COUNT. */
    @Override
    public int getThreatLevel() {
        int count = calculateThreatScore();
        if (count >= ALERT_MIN_COUNT)   return ALERT;    // 60+
        if (count >= CAUTION_MIN_COUNT) return CAUTION;  // 15..59
        return SAFE;                                     // 0..14
    }
}
