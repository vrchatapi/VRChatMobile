package dahun.co.kr.vrchat_test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

public class AutoRunBroadcastReceiver extends BroadcastReceiver{



    @Override

    public void onReceive(Context context, Intent intent) {



        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            SharedPreferences shared = context.getSharedPreferences("AutoLogin", Context.MODE_PRIVATE);
            if (shared.contains("id") && shared.contains("pwd")) {
                Intent i = new Intent(context, DataUpdateService.class);
                context.startService(i);
                DataUpdateService.showNotification(context, "서비스를 시작했습니다.", -2);
            }
            else{
                DataUpdateService.showNotification(context, "서비스를 시작하지 않았습니다.", -2);
            }
        }

    }

}
