package com.wakelight;
import android.content.Context;
import android.hardware.camera2.*;
import android.os.Build;
import java.lang.reflect.Method;
final class Torch {
 final CameraManager manager; final String id; Method samsung;
 Torch(Context c) throws Exception {
  manager=(CameraManager)c.getSystemService(Context.CAMERA_SERVICE);
  String found=null;
  for(String candidate:manager.getCameraIdList()) {
   CameraCharacteristics ch=manager.getCameraCharacteristics(candidate);
   if(Boolean.TRUE.equals(ch.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)) && Integer.valueOf(1).equals(ch.get(CameraCharacteristics.LENS_FACING))) { found=candidate; break; }
  }
  if(found==null) throw new Exception("Geen achterste zaklamp gevonden."); id=found;
  if(Build.VERSION.SDK_INT<33) {
   try { samsung=manager.getClass().getMethod("setTorchMode",String.class,boolean.class,int.class); }
   catch(NoSuchMethodException e) { throw new Exception("Deze Android-versie geeft apps geen toegang tot de vijf Samsung-standen."); }
  } else {
   Integer max=manager.getCameraCharacteristics(id).get(CameraCharacteristics.FLASH_INFO_STRENGTH_MAXIMUM_LEVEL);
   if(max==null || max<5) throw new Exception("De telefoon biedt minder dan vijf zaklampstanden aan apps.");
  }
 }
 void level(int n) throws Exception {
  if(Build.VERSION.SDK_INT>=33) manager.turnOnTorchWithStrengthLevel(id,n);
  else samsung.invoke(manager,id,true,n);
 }
 void off() {try {manager.setTorchMode(id,false);}catch(Exception ignored){}}
}
