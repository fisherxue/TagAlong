package com.tagalong.tagalong;

import android.util.TimingLogger;

public class TripListTimmingLogger {
  private static TripListTimmingLogger tripListTimmingLogger;
  private TimingLogger timingLogger;
  private final String TAG = "TripListTimingLogger";

  private TripListTimmingLogger(){
    timingLogger = new TimingLogger(TAG, "Trip List Timing Logger");
  }

  public static synchronized TripListTimmingLogger getInstance(){
    if (tripListTimmingLogger == null){
      tripListTimmingLogger = new TripListTimmingLogger();
    }
    return tripListTimmingLogger;
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