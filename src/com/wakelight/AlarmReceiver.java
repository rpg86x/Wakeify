package com.wakelight;
import android.content.*;
public class AlarmReceiver extends BroadcastReceiver {
 public void onReceive(Context c,Intent i) { c.getSharedPreferences("wake",0).edit().remove("next").apply(); c.startForegroundService(new Intent(c,LightService.class)); }
}
