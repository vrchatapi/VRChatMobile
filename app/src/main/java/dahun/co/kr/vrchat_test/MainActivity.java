package dahun.co.kr.vrchat_test;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.MainThread;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

/*
현재 상황 -> TaskThread의 run에서 VRCWorld.fetch를 여러번 실행하는 부분이 있는데, fetch가 서버에 request를 보내고 받는 과정이 들어있어서 오래걸림. 즉, 사용자에게 있어 렉을 유발한다.
fetch를 최소한으로 사용하여 해결할 수 있도록 해보자. (173번 줄 부근)
 */
public class MainActivity extends AppCompatActivity {

    static UserInfomationAdapter rvAdapter;
    static ViewUpdateThread viewUpdateThread = new ViewUpdateThread();
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
        toolbar.setTitle("접속한 친구 목록");


        setSupportActionBar(toolbar);

        Intent serviceIntent = new Intent(this, DataUpdateService.class);
        Log.i("2.friendsInfomation", "초기화 완료 : " + (DataUpdateService.friendsInfomation == null?"null":"not null"));

        rvAdapter = new UserInfomationAdapter(DataUpdateService.friendsInfomation, this);
        rv_Friends = findViewById(R.id.friends_recycler);
        rv_Friends.setLayoutManager(new LinearLayoutManager(this));
        rv_Friends.setAdapter(rvAdapter);

        startService(serviceIntent);

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
        if (viewUpdateThread.getState() == Thread.State.TERMINATED) {
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




}
