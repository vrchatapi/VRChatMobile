package dahun.co.kr.vrchat_test;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import dahun.co.kr.vrchat_test.API.VRCUser;

public class DataUpdateService extends Service {

    static ArrayList<UserInfomation> friendsInfomation = new ArrayList<>();;
    static Context context;
    static DataUpdateThread updater;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("Service", "onBind() 호출");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        friendsInfomation.clear();
        Log.i("1.friendsInfomation", "초기화 완료 : " + (friendsInfomation == null?"null":"not null"));
        context = this;
        updater = new DataUpdateThread();
        Log.i("Thread run", "call run()");
        SharedPreferences shared = getSharedPreferences("AutoLogin", MODE_PRIVATE);
        if (shared.contains("id") && shared.contains("pwd")) {
            String id = shared.getString("id", null);
            String pwd = shared.getString("pwd", null);
            login(id, pwd);
        }
        Log.i("LoginResult", "" + VRCUser.isLoggedIn());
        DataUpdateThread updater = new DataUpdateThread();
        updater.start();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

    }



    public boolean login(final String id, final String pwd) {
        Thread t = new Thread() {
            @Override
            public void run() {
                VRCUser.login(id, pwd);
            }
        };
        t.start();
        try {
            t.join();
        } catch (Exception e) {

        }
        return VRCUser.isLoggedIn();
    }

    static public void showNotification(Context context, String comment, int id) {

        // 현재시간을 msec 으로 구한다.
        long now = System.currentTimeMillis();
        // 현재시간을 date 변수에 저장한다.
        Date date = new Date(now);
        // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
        SimpleDateFormat sdfNow = new SimpleDateFormat("MM월 dd일 HH시 mm분");
        // nowDate 변수에 값을 저장한다.
        String formatDate = sdfNow.format(date);

        Notification n = new Notification.Builder(context)
                .setContentTitle(comment)
                .setContentText("접속한 시간 : " + formatDate)
                .setSmallIcon(R.drawable.vrc_icon4)
                .setAutoCancel(true).build();


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(id, n);

    }
}
