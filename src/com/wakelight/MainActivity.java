package com.wakelight;
import android.app.*;
import android.content.*;
import android.os.*;
import android.provider.Settings;
import android.net.Uri;
import android.widget.*;
import java.util.*;
public class MainActivity extends Activity {
 TextView status; TimePicker picker; Torch testTorch; boolean verified=false;
 void message(String s){status.setText(s);}
 Button button(LinearLayout root,String text,android.view.View.OnClickListener click){Button b=new Button(this);b.setText(text);root.addView(b);b.setOnClickListener(click);return b;}
 public void onCreate(Bundle state){super.onCreate(state);
  LinearLayout root=new LinearLayout(this);root.setOrientation(1);root.setPadding(32,40,32,24);root.setBackgroundColor(0xff101c28);ScrollView scroll=new ScrollView(this);scroll.addView(root);setContentView(scroll);
  TextView title=new TextView(this);title.setText("Wakeify ☀");title.setTextSize(32);root.addView(title);
  TextView intro=new TextView(this);intro.setText("Galaxy S10 · testversie\n\nVanaf je alarmtijd: 2 minuten per stand, van 1 tot 5. Na 10 minuten gaat het licht uit.\n\nTest eerst of jouw toestel vijf verschillende standen ondersteunt. Een alarm werkt eenmalig; stel het na een herstart opnieuw in.");root.addView(intro);
  picker=new TimePicker(this);picker.setIs24HourView(true);root.addView(picker);
  status=new TextView(this);status.setTextSize(16);root.addView(status);
  button(root,"1. Test de vijf standen",v->test());
  button(root,"2. Ik zag vijf verschillende helderheden",v->{if(testTorch==null){message("Voer eerst de test uit.");return;}testTorch.off();testTorch=null;verified=true;message("Test bevestigd. Je kunt nu het alarm instellen.");});
  button(root,"Lichtalarm instellen",v->schedule());
  button(root,"Stop licht / annuleer alarm",v->{alarm().cancel(operation());getSharedPreferences("wake",0).edit().remove("next").apply();stopService(new Intent(this,LightService.class));if(testTorch!=null){testTorch.off();testTorch=null;}message("Licht uit en alarm geannuleerd.");});
  message("Nog geen toesteltest uitgevoerd.");
 }
 void test(){
  if(checkSelfPermission("android.permission.CAMERA")!=0){requestPermissions(new String[]{"android.permission.CAMERA"},1);message("Geef cameratoegang en druk opnieuw op Test.");return;}
  verified=false;stopService(new Intent(this,LightService.class));
  try {if(testTorch!=null)testTorch.off();testTorch=new Torch(this);testTorch.level(1);message("Test: stand 1. Elke 3 seconden volgt de volgende stand.");
   Handler h=new Handler(getMainLooper());final Torch current=testTorch;
   for(int n=2;n<=5;n++){final int level=n;h.postDelayed(()->{if(testTorch!=current)return;try{current.level(level);message("Test: stand "+level+" van 5.");}catch(Exception e){current.off();testTorch=null;message("Test mislukt: "+e.getMessage());}},(n-1)*3000);}
   h.postDelayed(()->{if(testTorch==current){current.off();message("Test klaar. Bevestig alleen als je vijf verschillende helderheden zag.");}},15000);
  }catch(Exception e){testTorch=null;message(e.getMessage());}
 }
 AlarmManager alarm(){return getSystemService(AlarmManager.class);}
 PendingIntent operation(){return PendingIntent.getBroadcast(this,1,new Intent(this,AlarmReceiver.class),PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT);}
 void schedule(){
  if(!verified){message("Test en bevestig eerst de vijf helderheden.");return;}
  if(Build.VERSION.SDK_INT>=31 && !alarm().canScheduleExactAlarms()){startActivity(new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,Uri.parse("package:"+getPackageName())));message("Sta exacte alarmen toe en druk opnieuw op instellen.");return;}
  Calendar c=Calendar.getInstance();c.set(Calendar.HOUR_OF_DAY,picker.getHour());c.set(Calendar.MINUTE,picker.getMinute());c.set(Calendar.SECOND,0);c.set(Calendar.MILLISECOND,0);if(c.getTimeInMillis()<=System.currentTimeMillis())c.add(Calendar.DATE,1);
  PendingIntent show=PendingIntent.getActivity(this,0,new Intent(this,MainActivity.class),PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT);
  alarm().setAlarmClock(new AlarmManager.AlarmClockInfo(c.getTimeInMillis(),show),operation());getSharedPreferences("wake",0).edit().putLong("next",c.getTimeInMillis()).apply();message("Licht begint op "+android.text.format.DateFormat.format("dd-MM HH:mm",c)+". Totale duur: 10 minuten.");
 }
 protected void onResume(){super.onResume();String err=getSharedPreferences("wake",0).getString("error",null);if(err!=null){message(err);getSharedPreferences("wake",0).edit().remove("error").apply();}else {long next=getSharedPreferences("wake",0).getLong("next",0);if(next>System.currentTimeMillis())message("Alarm: "+android.text.format.DateFormat.format("dd-MM HH:mm",next));}}
 protected void onPause(){if(testTorch!=null){testTorch.off();testTorch=null;}super.onPause();}
}
