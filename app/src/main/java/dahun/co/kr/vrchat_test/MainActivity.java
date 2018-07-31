package dahun.co.kr.vrchat_test;

import android.animation.ObjectAnimator;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dahun.co.kr.vrchat_test.API.FriendStatus;
import dahun.co.kr.vrchat_test.API.VRCFriends;
import dahun.co.kr.vrchat_test.API.VRCUser;
import dahun.co.kr.vrchat_test.API.VRCWorld;

public class MainActivity extends AppCompatActivity {

    ArrayList<UserInfomation> friendsInfomation;
    static UserInfomationAdapter rvAdapter;
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

        // ** 주의! **
        // API를 사용하는 모든 작업은 Main스레드에서 사용하면 정상동작 하지 않음!
        // 그 외의 서브 스레드에서만 API에 접근할 것!

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("접속한 친구 목록");


        setSupportActionBar(toolbar);

        friendsInfomation = new ArrayList<>();
        friendsInfomation.clear();
        rvAdapter = new UserInfomationAdapter(friendsInfomation, this);
        rv_Friends = findViewById(R.id.friends_recycler);
        rv_Friends.setLayoutManager(new LinearLayoutManager(this));
        rv_Friends.setAdapter(rvAdapter);


        Intent i = getIntent();
        id = i.getStringExtra("id");
        pwd = i.getStringExtra("pwd");

        final TaskThread tt = new TaskThread();
        tt.start();

        // 플로팅액션버튼 눌렀을떄 이벤트 설정
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tt.update();
            }
        });
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            Intent i = new Intent(this, LoginActivity.class);
            SharedPreferences.Editor editor = LoginActivity.shared.edit();
            editor.remove("id");
            editor.remove("pwd");
            editor.commit();
            startActivity(i);
            finish();
            return true;
        }
        if (id == R.id.action_help) {
            return true;
        }
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_feedback) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    class TaskThread extends Thread {

        boolean updateFlag = true;
        int num_notification = 0;
        boolean notification_mode = false;

        public void update() {
            updateFlag = true;
        }

        @Override
        public void run() {
            //super.run();
            Log.i("Thread run", "call run()");
            login(id, pwd);

            while (true) {

                for (int i = 0; i < 15; i++) {
                    if (!updateFlag) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                updateFlag = false;

                List<UserInfomation> copy_fi = (List<UserInfomation>) friendsInfomation.clone();

                final List<VRCUser> list_Friends = VRCFriends.fetchFriends(FriendStatus.ONLINE);

                final ArrayList<VRCWorld> list_Friends_world = new ArrayList<VRCWorld>();


                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {    // 오래 거릴 작업을 구현한다
                        // TODO Auto-generated method stub

                        for (int i = 0; i < list_Friends.size(); i++) {
                            String[] world_ins = list_Friends.get(i).getLocation().split(":|~");
                            VRCWorld.fetch(world_ins[0]);
                            //VRCWorld.fetch(world_ins[0]).getName() + ":" + (world_ins.length != 1 ? world_ins[1] : "")
                        }


                    }
                });

                for (int i = 0; i < list_Friends.size(); i++) {

                    if (list_Friends.get(i).getLocation().equals("private")) {
                        //Log.v("Friends",list_Friends.get(i).toString() + "  위치 : private");
                        if (friendsInfomation.size() > i)
                            friendsInfomation.set(i, new UserInfomation(list_Friends.get(i).getCurrentAvatarThumbnailImageUrl(), list_Friends.get(i).getDisplayName().toString(), "private"));
                        else
                            friendsInfomation.add(new UserInfomation(list_Friends.get(i).getCurrentAvatarThumbnailImageUrl(), list_Friends.get(i).getDisplayName().toString(), "private"));
                    } else {
                        String[] world_ins = list_Friends.get(i).getLocation().split(":|~");
                        //Log.v("Friends",list_Friends.get(i).toString() + "  위치 : " + VRCWorld.fetch(world_ins[0]).getName() + ":" + world_ins[1]);
                        if (friendsInfomation.size() > i)
                            //friendsInfomation.set(i, new UserInfomation(list_Friends.get(i).getCurrentAvatarThumbnailImageUrl(), list_Friends.get(i).getDisplayName().toString(), VRCWorld.fetch(world_ins[0]).getName() + ":" + (world_ins.length != 1 ? world_ins[1] : "")));
                            friendsInfomation.set(i, new UserInfomation(list_Friends.get(i).getCurrentAvatarThumbnailImageUrl(), list_Friends.get(i).getDisplayName().toString(), VRCWorld.fetch(world_ins[0]).getName() + ":" + (world_ins.length != 1 ? world_ins[1] : "")));
                        else
                            //friendsInfomation.add(new UserInfomation(list_Friends.get(i).getCurrentAvatarThumbnailImageUrl(), list_Friends.get(i).getDisplayName().toString(), VRCWorld.fetch(world_ins[0]).getName() + ":" + (world_ins.length != 1 ? world_ins[1] : "")));
                            friendsInfomation.add(new UserInfomation(list_Friends.get(i).getCurrentAvatarThumbnailImageUrl(), list_Friends.get(i).getDisplayName().toString(), VRCWorld.fetch(world_ins[0]).getName() + ":" + (world_ins.length != 1 ? world_ins[1] : "")));
                    }
                    Log.i("ImageURI", list_Friends.get(i).getCurrentAvatarThumbnailImageUrl());
                    MainActivity.handler.sendEmptyMessage(0);


                }

                Log.i("before DataList Size", "" + copy_fi.size());
                Log.i("after DataList Size", "" + friendsInfomation.size());
                if (notification_mode == true) {
                    for (int i = 0; i < friendsInfomation.size(); i++) {
                        boolean isContains = false;
                        for (int j = 0; j < copy_fi.size(); j++) {
                            if (friendsInfomation.get(i).getDisplayName().equals(copy_fi.get(j).getDisplayName())) {
                                isContains = true;
                                break;
                            }
                        }
                        if (isContains == false)
                            showNotification(friendsInfomation.get(i).getDisplayName() + "님이 접속했습니다.", num_notification++);
                    }
                } else {
                    notification_mode = true;
                }
            }
        }

        public boolean login(String id, String pwd) {
            VRCUser.login(id, pwd);
            return VRCUser.isLoggedIn();
        }

        public void showNotification(String comment, int id) {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            PendingIntent pIntent = PendingIntent.getActivity(MainActivity.this, (int) System.currentTimeMillis(), intent, 0);

            // 현재시간을 msec 으로 구한다.
            long now = System.currentTimeMillis();
            // 현재시간을 date 변수에 저장한다.
            Date date = new Date(now);
            // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
            SimpleDateFormat sdfNow = new SimpleDateFormat("MM월 dd일 HH시 mm분");
            // nowDate 변수에 값을 저장한다.
            String formatDate = sdfNow.format(date);

            Notification n = new Notification.Builder(MainActivity.this)
                    .setContentTitle(comment)
                    .setContentText("접속한 시간 : " + formatDate)
                    .setSmallIcon(R.drawable.vrc_icon4)
                    //.setContentIntent(pIntent)
                    .setAutoCancel(true).build();


            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            notificationManager.notify(id, n);

        }
    }

}
