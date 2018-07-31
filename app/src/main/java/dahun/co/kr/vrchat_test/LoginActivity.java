package dahun.co.kr.vrchat_test;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

import dahun.co.kr.vrchat_test.API.VRCUser;

import static dahun.co.kr.vrchat_test.UserInfomationAdapter.getRoundedCornerBitmap;

public class LoginActivity extends AppCompatActivity {

    static SharedPreferences shared;
    ImageView titleImageView;
    EditText idEditText;
    EditText pwdEditText;
    Button loginButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AppCompat_DayNight_NoActionBar);
        setContentView(R.layout.activity_login);

        titleImageView = findViewById(R.id.login_titleImageView);
        idEditText = findViewById(R.id.login_idEditText);
        pwdEditText = findViewById(R.id.login_pwdEditText);
        loginButton = findViewById(R.id.login_loginButton);

        shared = getSharedPreferences("AutoLogin", MODE_PRIVATE);
        if (shared.contains("id") && shared.contains("pwd")) {
            idEditText.setText(shared.getString("id", null));
            pwdEditText.setText(shared.getString("pwd", null));
            checkLogin();
        }

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {   
                // TODO Auto-generated method stub
                try {
                    URL url = new URL("https://steamcdn-a.akamaihd.net/steam/apps/438100/header.jpg"); // Change to local resource.
                    InputStream is = url.openStream();
                    Bitmap bm = BitmapFactory.decodeStream(is);
                    bm = getRoundedCornerBitmap(bm, 20);
                    titleImageView.setImageBitmap(bm); //비트맵 객체로 보여주기
                } catch (Exception e) {

                }

            }
        });
        t.start();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkLogin();
            }
        });




    }
    private void checkLogin(){
        Thread t = new Thread(){
            @Override
            public void run() {
                VRCUser.login(idEditText.getText().toString(), pwdEditText.getText().toString());
            }
        };
        t.start();
        try {
            t.join();
        }
        catch(Exception e){

        }

        if (VRCUser.isLoggedIn() == false)
            return;


        SharedPreferences.Editor editor = shared.edit();
        editor.putString("id", idEditText.getText().toString());
        editor.putString("pwd", pwdEditText.getText().toString());
        editor.commit();

        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        i.putExtra("id", idEditText.getText().toString());
        i.putExtra("pwd", pwdEditText.getText().toString());

        startActivity(i);
        finish();
    }

}
