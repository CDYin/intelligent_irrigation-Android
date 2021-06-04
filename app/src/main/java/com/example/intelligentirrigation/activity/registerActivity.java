package com.example.intelligentirrigation.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.intelligentirrigation.impl.AndroidExitimpl;
import com.example.intelligentirrigation.DefaultData;
import com.example.intelligentirrigation.R;
import com.example.intelligentirrigation.TCPSocketClient;
import com.example.intelligentirrigation.utils.ToBeMD5;

import java.io.IOException;

public class registerActivity extends AppCompatActivity implements View.OnClickListener{

    private RelativeLayout RlRegisteractivityone;
    private LinearLayout LlRegisteractivity_body;
    private TextView TvRegisteractivity_username;
    private EditText EtRegisteractivity_username;
    private TextView TvRegisteractivity_password1;
    private EditText EtRegisteractivity_password1;
    private TextView TvRegisteractivity_password2;
    private EditText EtRegisteractivity_password2;
    private ImageView IvRegisteractivity_back;
    private ImageView IvRegisteractivity_close;
    private Button BtRegisteractivity_register;
    private Context myContext;

    private TCPSocketClient tcpSocketClient = TCPSocketClient.sharedCenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        myContext = this;
        initView();
    }

    private void initView() {
        EtRegisteractivity_username = findViewById(R.id.et_registeractivity_username);
        EtRegisteractivity_password1 = findViewById(R.id.et_registeractivity_password1);
        EtRegisteractivity_password2 = findViewById(R.id.et_registeractivity_password2);
        IvRegisteractivity_back = findViewById(R.id.iv_registeractivity_back);
        IvRegisteractivity_close = findViewById(R.id.iv_registeractivity_close);
        BtRegisteractivity_register = findViewById(R.id.bt_registeractivity_register);


        IvRegisteractivity_back.setOnClickListener(this);
        IvRegisteractivity_close.setOnClickListener(this);
        BtRegisteractivity_register.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_registeractivity_back:
                tcpSocketClient.disconnect();
                Intent intent1 = new Intent(this, loginActivity.class);
                startActivity(intent1);
                finish();
                break;
            case R.id.iv_registeractivity_close:
                    AndroidExitimpl androidExitimpl = new AndroidExitimpl();
                    androidExitimpl.DialogShow(myContext);
                break;
            case R.id.bt_registeractivity_register:
                String username = EtRegisteractivity_username.getText().toString().trim();
                String password1 = EtRegisteractivity_password1.getText().toString().trim();
                String password2 = EtRegisteractivity_password2.getText().toString().trim();
                if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password1) && !TextUtils.isEmpty(password2)){
                    if (password1.equals(password2)){
                        //将用户名和密码加入到数据库中
                        String sendMsg = "{\"username\":\"" + username + "\",\"type\":\"10\",\"password\":\"" + ToBeMD5.getMD5(password1) + "\"}";
                        String result = null;
                        try {
                            tcpSocketClient.connect(DefaultData.SOCKET_IP,DefaultData.SOCKET_PORT);
                            tcpSocketClient.send(sendMsg);
                            tcpSocketClient.receive();
                            result = tcpSocketClient.getRegistTask_Map();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (result.equals("true")) {
                            System.out.println("用户数据保存成功");
                            tcpSocketClient.setRegisterTask_Map("false");
                            Intent intent2 = new Intent(this, mainActivity.class);
                            startActivity(intent2);
                            finish();
                            Toast.makeText(this, "验证通过，注册成功", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(this, "用户已存在，注册失败", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(this, "两次密码输入不同", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                }
        }
    }
}
