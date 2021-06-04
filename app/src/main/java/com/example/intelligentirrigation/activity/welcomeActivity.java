package com.example.intelligentirrigation.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.intelligentirrigation.DefaultData;
import com.example.intelligentirrigation.R;
import com.example.intelligentirrigation.TCPSocketClient;

import java.util.Timer;
import java.util.TimerTask;

import lombok.SneakyThrows;

public class welcomeActivity extends AppCompatActivity {
    private ImageView IvWeicomeactivityTUTE;
    private Timer timer;

    private Context myContext;

    private TCPSocketClient tcpSocketClient = TCPSocketClient.sharedCenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myContext = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_welcome);
        IvWeicomeactivityTUTE = findViewById(R.id.iv_welcomeactivityTUTE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        timer = new Timer(true);

        SharedPreferences sharedPreferences = getSharedPreferences("sp",MODE_PRIVATE);
        String username = sharedPreferences.getString("username","");
        String password = sharedPreferences.getString("password","");

        if (username.equals("")){
            timer.schedule(login,3000);
        }else {
            timer.schedule(main,3000);
        }
    }

    TimerTask login = new TimerTask() {
        @Override
        public void run() {
            Intent intent = new Intent(welcomeActivity.this, loginActivity.class);
            startActivity(intent);
            finish();
        }
    };

    TimerTask main = new TimerTask() {
        @SneakyThrows
        @Override
        public void run() {
            tcpSocketClient.connect(DefaultData.SOCKET_IP,DefaultData.SOCKET_PORT);
            Intent intent = new Intent(welcomeActivity.this, mainActivity.class);
            startActivity(intent);
            finish();
        }
    };

}
