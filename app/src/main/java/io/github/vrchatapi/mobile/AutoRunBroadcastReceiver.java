package io.github.vrchatapi.mobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class AutoRunBroadcastReceiver extends BroadcastReceiver{



    @Override

    public void onReceive(Context context, Intent intent) {
        SharedPreferences shared = context.getSharedPreferences("AutoLogin", Context.MODE_PRIVATE);
        Log.i("BroadcastReceiver", "onReceive 호출");
        if(intent.getAction().equals("ACTION.RESTART.PersistentService")) {
            Log.i("BroadcastReceiver", "ACTION.RESTART.PersistentService 감지");
            if (shared.contains("id") && shared.contains("pwd")) {
                Intent i = new Intent(context, DataUpdateService.class);
                context.startService(i);
                //DataUpdateService.showNotification(context, "서비스를 시작했습니다.", -2);
            }
            else{
                //DataUpdateService.showNotification(context, "서비스를 시작하지 않았습니다.", -2);
            }
        }


        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.i("BroadcastReceiver", "Intent.ACTION_BOOT_COMPLETED 감지");
            if (shared.contains("id") && shared.contains("pwd")) {
                Intent i = new Intent(context, DataUpdateService.class);
                context.startService(i);
                //DataUpdateService.showNotification(context, "서비스를 시작했습니다.", -2);
            }
            else{
                //DataUpdateService.showNotification(context, "서비스를 시작하지 않았습니다.", -2);
            }
        }

    }

}
