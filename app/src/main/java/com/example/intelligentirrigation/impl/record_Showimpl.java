package com.example.intelligentirrigation.impl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.bin.david.form.core.SmartTable;
import com.example.intelligentirrigation.R;
import com.example.intelligentirrigation.entity.Record;
import com.example.intelligentirrigation.TCPSocketClient;
import com.example.intelligentirrigation.activity.mainActivity;

import java.util.List;

public class record_Showimpl extends AppCompatActivity implements View.OnClickListener{

    private RelativeLayout RlRecordactivityone;
    private ImageView IvRecordactivityClose;
    private ImageView IvRecordactivityBack;
    private RelativeLayout RlRecordactivitytwo;
    private TableLayout TlRecordavtivity_show;
    private SmartTable table;

    private Context myContext;

    private TCPSocketClient tcpSocketClient = TCPSocketClient.sharedCenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_show);
        myContext = this;
        initView();
    }


    private void initView() {

        RlRecordactivityone = findViewById(R.id.rl_recordactivity_one);
        IvRecordactivityClose = findViewById(R.id.iv_recordactivity_close);
        IvRecordactivityBack = findViewById(R.id.iv_recordactivity_back);
        table = findViewById(R.id.record_table);

        WindowManager wm = this.getWindowManager();
        int screenWith = wm.getDefaultDisplay().getWidth();
        table.getConfig().setMinTableWidth(screenWith); //设置最小宽度 屏幕宽度

        initTableView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_recordactivity_back :
                Intent intent1 = new Intent(this, mainActivity.class);
                startActivity(intent1);
                finish();
                break;
            case R.id.iv_recordactivity_close :
                AndroidExitimpl androidExitimpl = new AndroidExitimpl();
                androidExitimpl.DialogShow(myContext);
                break;
        }
    }


    private void initTableView() {
        System.out.println();
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        String CurrentDate = bundle.getString("CurrentDate");
        System.out.println(CurrentDate);
        String sendMsg = "{\"date\":\""+CurrentDate+"\",\"type\":\"11\"}";
        tcpSocketClient.send(sendMsg);
        tcpSocketClient.receive();
        List<Record> list= tcpSocketClient.getsplit_result();
        table.setData(list);
//        TableData tableData = new TableData<>("数据",list,temperatureColumn,humidityColumn,currentTimeColumn,remarkColumn);
//        table.setData((List) tableData);
        tcpSocketClient.clear_List();
    }
}
