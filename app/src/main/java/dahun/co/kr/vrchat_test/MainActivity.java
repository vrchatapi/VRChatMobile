package dahun.co.kr.vrchat_test;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import dahun.co.kr.vrchat_test.API.VRCUser;
import dahun.co.kr.vrchat_test.API.FriendStatus;
import dahun.co.kr.vrchat_test.API.VRCFriends;
import dahun.co.kr.vrchat_test.R;

/*
현재 상황 -> TaskThread의 run에서 VRCWorld.fetch를 여러번 실행하는 부분이 있는데, fetch가 서버에 request를 보내고 받는 과정이 들어있어서 오래걸림. 즉, 사용자에게 있어 렉을 유발한다.
fetch를 최소한으로 사용하여 해결할 수 있도록 해보자. (173번 줄 부근)
 */
public class MainActivity extends AppCompatActivity {
    static UserInfomationAdapter rvAdapter;
    static ViewUpdateThread viewUpdateThread = new ViewUpdateThread();
    Intent serviceIntent;
    RecyclerView rv_Friends;
    Toolbar toolbar;
    String id, pwd;

    static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == 0) {

                rvAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.connectFriendsList));


        setSupportActionBar(toolbar);
        serviceIntent = new Intent(this, DataUpdateService.class);

        //Intent serviceIntent = new Intent(this, DataUpdateService.class);
        Log.i("2.friendsInfomation", "초기화 완료 : " + (DataUpdateService.updater.friendsInfomation == null ? "null" : "not null"));

        rvAdapter = new UserInfomationAdapter(DataUpdateService.updater.friendsInfomation, this);
        rv_Friends = findViewById(R.id.friends_recycler);
        rv_Friends.setLayoutManager(new LinearLayoutManager(this));
        rv_Friends.setAdapter(rvAdapter);

        if (!isServiceRunningCheck("dahun.co.kr.vrchat_test", "DataUpdateService")) {   // 이거 제대로 되고있는거 맞나...?
            serviceIntent = new Intent(this, DataUpdateService.class);
            startService(serviceIntent);
        }

        //viewUpdateThread.start();

        Intent i = getIntent();
        id = i.getStringExtra("id");
        pwd = i.getStringExtra("pwd");


        // 플로팅액션버튼 눌렀을떄 이벤트 설정
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataUpdateService.updater.update();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (viewUpdateThread.runningFlag == false) {
            viewUpdateThread = new ViewUpdateThread();
        }
        viewUpdateThread.start();
        Log.i("ViewUpdateThread", "시작");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (viewUpdateThread.isAlive())
            viewUpdateThread.terminate();
        Log.i("ViewUpdateThread", "종료");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int itemId = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (itemId == R.id.action_logout) {
            Intent i = new Intent(this, LoginActivity.class);
            SharedPreferences.Editor editor = LoginActivity.shared.edit();
            editor.remove("id");
            editor.remove("pwd");
            editor.commit();
            DataUpdateService.updater.terminate();
            stopService(serviceIntent);
            startActivity(i);
            finish();
            return true;
        }
        if (itemId == R.id.action_help) {
            return true;
        }
        if (itemId == R.id.action_notiON) {
            DataUpdateThread.notification_mode = true;
            SharedPreferences.Editor editor = DataUpdateService.context.getSharedPreferences(DataUpdateThread.SETTING_SAVE, MODE_PRIVATE).edit();
            editor.putBoolean(id + "notification_mode", true);
            editor.commit();
            return true;
        }
        if (itemId == R.id.action_notiOFF) {
            DataUpdateThread.notification_mode = false;
            SharedPreferences.Editor editor = DataUpdateService.context.getSharedPreferences(DataUpdateThread.SETTING_SAVE, MODE_PRIVATE).edit();
            editor.putBoolean(id + "notification_mode", false);
            editor.commit();
            return true;
        }
        if (itemId == R.id.action_friendnotiON) {
            DataUpdateThread.notificationList.clear();

            Thread t = new Thread() {
                @Override
                public void run() {
                    List<VRCUser> list_Friends = VRCFriends.fetchFriends(FriendStatus.ONLINE);
                    for (int i = 0; i < list_Friends.size(); i++) {
                        DataUpdateThread.notificationList.add(list_Friends.get(i).getDisplayName());
                    }
                    list_Friends = VRCFriends.fetchFriends(FriendStatus.OFFLINE);
                    for (int i = 0; i < list_Friends.size(); i++) {
                        DataUpdateThread.notificationList.add(list_Friends.get(i).getDisplayName());
                    }
                    Log.i("notificationList", list_Friends.size() + DataUpdateThread.notificationList.toString());
                    SharedPreferences.Editor editor = DataUpdateService.context.getSharedPreferences(DataUpdateThread.SETTING_SAVE, MODE_PRIVATE).edit();
                    editor.putInt(id + "notificationList_size", DataUpdateThread.notificationList.size());
                    for (int i = 0; i < DataUpdateThread.notificationList.size(); i++) {
                        editor.putString(id + "notificationList" + i, DataUpdateThread.notificationList.get(i));
                    }
                    editor.commit();
                }
            };
            t.start();
            return true;
        }
        if (itemId == R.id.action_friendnotiOFF) {
            DataUpdateThread.notificationList.clear();
            SharedPreferences.Editor editor = DataUpdateService.context.getSharedPreferences(DataUpdateThread.SETTING_SAVE, MODE_PRIVATE).edit();
            editor.putInt(id + "notificationList_size", 0);
            editor.commit();
            return true;
        }
        if (itemId == R.id.action_settings) {
            return true;
        }
        if (itemId == R.id.action_feedback) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isServiceRunningCheck(String packageName, String serviceName) {
        ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            //Toast.makeText(this,  "현재 " + service.service.getShortClassName() + " 실행되어있습니다.", Toast.LENGTH_SHORT).show();
            Log.i("ServiceList", service.service.getClassName());
            if ((packageName + "." + serviceName).equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}
