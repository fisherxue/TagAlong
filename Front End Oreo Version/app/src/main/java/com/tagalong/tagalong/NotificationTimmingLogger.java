package com.tagalong.tagalong;

import android.util.TimingLogger;

public class NotificationTimmingLogger {

    private static NotificationTimmingLogger notificationTimmingLogger;
    private TimingLogger timingLogger;
    private final String TAG = "NotificationTimingLogger";

    private NotificationTimmingLogger(){
        timingLogger = new TimingLogger(TAG, "Notification Timing Logger");
    }

    public static synchronized NotificationTimmingLogger getInstance(){
        if (notificationTimmingLogger == null){
            notificationTimmingLogger = new NotificationTimmingLogger();
        }
        return notificationTimmingLogger;
    }

    public void dumpToLog() {
        timingLogger.dumpToLog();
    }

    public void addSplit(String string) {
        timingLogger.addSplit(string);
    }

    public void reset() {
        timingLogger.reset();
    }
}
