package com.example.intelligentirrigation.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.example.intelligentirrigation.impl.AndroidExitimpl;
import com.example.intelligentirrigation.DefaultData;
import com.example.intelligentirrigation.R;
import com.example.intelligentirrigation.TCPSocketClient;
import com.example.intelligentirrigation.utils.ToBeMD5;

import java.io.IOException;

import lombok.SneakyThrows;

public class loginActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout RlLoginactivityone;
    private LinearLayout LlLoginactivityTwo;
    private LinearLayout LlLoginactivitythree;
    private EditText EtLoginactivityUsername;
    private EditText EtLoginactivityPassword;
    private Button BtLoginactivityLogin;
    private Button BtLoginactivityRegister;
    private ImageView IvLoginactivityClose;
    private CheckBox CBLoginactivityRemember;
    //exit_manage_dialog.xml
    private Context myContext;
    private Button btExitDialogCancel;
    private Button btExitDialogSure;

    private TCPSocketClient tcpSocketClient = TCPSocketClient.sharedCenter();

    @SneakyThrows
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        myContext = this;
        initView();
        autoFill();
    }
    /**
     * onCreae()中大的布局已经摆放好了，接下来就该把layout里的东西
     * 声明、实例化对象然后有行为的赋予其行为
     * 这样就可以把视图层View也就是layout 与 控制层 Java 结合起来了
     */
    private void initView(){

        RlLoginactivityone = findViewById(R.id.rl_loginactivity_one);
        LlLoginactivityTwo = findViewById(R.id.ll_loginactivity_two);
        LlLoginactivitythree = findViewById(R.id.ll_loginactivity_three);
        EtLoginactivityUsername = findViewById(R.id.et_loginactivity_username);
        EtLoginactivityPassword = findViewById(R.id.et_loginactivity_password);
        BtLoginactivityLogin = findViewById(R.id.bt_loginactivity_login);
        BtLoginactivityRegister = findViewById(R.id.bt_loginactivity_regist);
        IvLoginactivityClose = findViewById(R.id.iv_loginactivity_close);
        CBLoginactivityRemember = findViewById(R.id.cb_loginactivity_remember);

        BtLoginactivityLogin.setOnClickListener(this);
        BtLoginactivityRegister.setOnClickListener(this);
        IvLoginactivityClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_loginactivity_login:
                String username = EtLoginactivityUsername.getText().toString().trim();
                String password = EtLoginactivityPassword.getText().toString().trim();
                if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                    String sendMsg = "{\"username\":\"" + username + "\",\"type\":\"00\",\"password\":\"" + ToBeMD5.getMD5(password) + "\"}";
                    String result = null;
                    System.out.println("准备发送数据");
                    try {
                        tcpSocketClient.connect(DefaultData.SOCKET_IP,DefaultData.SOCKET_PORT);
                        tcpSocketClient.send(sendMsg);
                        tcpSocketClient.receive();
                        result = tcpSocketClient.getLoginTask_Map();
                        System.out.println(result);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    if (result.equals("true")) {
                        tcpSocketClient.setLoginTask_Map("false");
                        Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
                        checkAndWrite(username, password);
                        Intent intent = new Intent(this, mainActivity.class);
                        startActivity(intent);
                        finish();//销毁此Activity
                    } else {
                        Toast.makeText(this, "用户名或密码不正确，请重新输入", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this, "请输入你的用户名或密码", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt_loginactivity_regist:
                startActivity(new Intent(this, registerActivity.class));
                /**
                 1、不使用finish()：从activity 1中启动activity 2，然后在activity 2 启动activity 3，这时按下返回键 程序就返回到了activity 2，再按下返回键 就返回到activity 1；
                 2、使用finish()：从activity 1中启动activity 2,在activity 2调用finish()，然后在activity 2 启动activity 3，这时按下返回键 程序就直接返回了activity 1
                 */
                finish();
                break;
            case R.id.iv_loginactivity_close:
                    AndroidExitimpl androidExitimpl = new AndroidExitimpl();
                    androidExitimpl.DialogShow(myContext);
                break;
        }
    }
    //保存用户名和密码
    private void checkAndWrite(String username,String password){
        SharedPreferences sharedPreferences = this.getSharedPreferences("sp",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(CBLoginactivityRemember.isChecked())
        {
            editor.putBoolean("remember_password",true);
            editor.putString("username",username);
            editor.putString("password",password);
        }else{
            editor.clear();
        }
        editor.apply();
    }
    //自动填入用户名和密码
    private void autoFill(){
        SharedPreferences sharedPreferences= this.getSharedPreferences("sp",Context.MODE_PRIVATE);
        boolean isRemenber=sharedPreferences.getBoolean("remember_password",false);
        if(isRemenber){
            EtLoginactivityUsername.setText(sharedPreferences.getString("username",""));
            EtLoginactivityPassword.setText(sharedPreferences.getString("password",""));
            CBLoginactivityRemember.setChecked(true);
        }
    }
}
