package com.wakelight;
import android.app.*;
import android.content.*;
import android.os.*;
public class LightService extends Service {
 Handler handler=new Handler(Looper.getMainLooper()); Torch torch; PowerManager.WakeLock lock; long began;
 public void onCreate() {
  super.onCreate(); NotificationManager nm=getSystemService(NotificationManager.class);
  nm.createNotificationChannel(new NotificationChannel("light","Lichtalarm",NotificationManager.IMPORTANCE_LOW));
 }
 Notification note(String text) {
  PendingIntent stop=PendingIntent.getService(this,2,new Intent(this,LightService.class).setAction("STOP"),PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT);
  return new Notification.Builder(this,"light").setSmallIcon(android.R.drawable.ic_lock_idle_alarm).setContentTitle("Wakeify").setContentText(text).setOngoing(true).addAction(android.R.drawable.ic_delete,"Stop",stop).build();
 }
 public int onStartCommand(Intent i,int flags,int startId) {
  startForeground(1,note("Lichtalarm starten"));
  if(i!=null && "STOP".equals(i.getAction())) {stopSelf();return START_NOT_STICKY;}
  if(torch!=null) return START_NOT_STICKY;
  try {
   torch=new Torch(this); lock=((PowerManager)getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"WakeLight:sequence"); lock.acquire(11*60*1000L);
   began=SystemClock.elapsedRealtime(); step.run();
  } catch(Exception e) {getSharedPreferences("wake",0).edit().putString("error","Lichtalarm mislukt: "+e.getMessage()).apply();stopSelf();}
  return START_NOT_STICKY;
 }
 Runnable step=new Runnable(){public void run(){
  long elapsed=SystemClock.elapsedRealtime()-began; int level=(int)(elapsed/120000)+1;
  if(level>5){stopSelf();return;}
  try {torch.level(level);getSystemService(NotificationManager.class).notify(1,note("Helderheid "+level+" van 5 · Stop om uit te schakelen"));handler.postDelayed(this,120000-elapsed%120000);}
  catch(Exception e){getSharedPreferences("wake",0).edit().putString("error","Zaklamp gestopt: "+e.getMessage()).apply();stopSelf();}
 }};
 public void onDestroy(){handler.removeCallbacksAndMessages(null);if(torch!=null)torch.off();if(lock!=null && lock.isHeld())lock.release();super.onDestroy();}
 public IBinder onBind(Intent i){return null;}
}
