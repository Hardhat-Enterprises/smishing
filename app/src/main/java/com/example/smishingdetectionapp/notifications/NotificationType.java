package com.example.smishingdetectionapp.notifications;

import android.app.NotificationManager;

public class NotificationType {
    // channel ID for the notification channel
    private String channelID;
    // name of the notification channel
    private String channelName;
    // description of the notification channel
    private String channelDesc;
    // importance level for the notification channel - determines how intrusive notifications are
    private int importance;
    // to determine if notifications from this channel should be dismissible
    private boolean ongoing;

    // NotificationType object constructor
    public NotificationType(String channelID, String channelName, String channelDesc, int importance, boolean ongoing)
    {
        this.channelID = channelID;
        this.channelName = channelName;
        this.channelDesc = channelDesc;
        this.importance = importance;
        this.ongoing = ongoing;
    }

    public String getChannelID()
    {
        return channelID;
    } // returns channel ID
    public String getChannelName()
    {
        return channelName;
    } // returns channel name
    public String getChannelDesc()
    {
        return channelDesc;
    } // returns channel description
    public int getImportance()
    {
        return importance;
    } // returns channel importance/priority
    public boolean getOngoing() {
        return ongoing;
    } // returns true if notification is ongoing/non-dismissible

    // predefined notification type
    public static final NotificationType DETECTION_ALERT = new NotificationType(
            "detections_channel",
            "Smishing Detection Alerts",
            "Channel for Smishing detection notifications",
            NotificationManager.IMPORTANCE_MAX,
            false);

    // predefined notification type
    public static final NotificationType NEWS_UPDATE = new NotificationType(
            "news_channel",
            "News Updates",
            "Channel for SMISHING and CyberSecurity related News notifications",
            NotificationManager.IMPORTANCE_DEFAULT,
            true);

}
