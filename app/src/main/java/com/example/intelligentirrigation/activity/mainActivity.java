package com.example.intelligentirrigation.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.intelligentirrigation.impl.AndroidAirShowimpl;
import com.example.intelligentirrigation.impl.AndroidExitimpl;
import com.example.intelligentirrigation.R;
import com.example.intelligentirrigation.entity.Record;
import com.example.intelligentirrigation.TCPSocketClient;
import com.example.intelligentirrigation.impl.record_Showimpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import lombok.SneakyThrows;

public class mainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String LOG_TAG = "mainActivity";

    private static final String[] mListData = {"显示当前空气温湿度","显示灌溉记录"};

    private RelativeLayout RlMainactivityone;
    private ImageView IvMainactivityClose;
    private ImageView IvMainactivityBack;
    private ListView LvMainactivityShow;
    private ArrayAdapter<String> mAdapter = null;
    private List mList = null;

    private DatePickerDialog datePickerDialog;
    private int year, monthOfYear, dayOfMonth;

    private Context myContext;

    private TCPSocketClient tcpSocketClient = TCPSocketClient.sharedCenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myContext = this;
        initView();
    }

    private void initView() {
        RlMainactivityone = findViewById(R.id.rl_mainactivity_one);
        IvMainactivityClose = findViewById(R.id.iv_mainactivity_close);
        IvMainactivityBack = findViewById(R.id.iv_mainactivity_back);
        LvMainactivityShow = findViewById(R.id.lv_mainactivity_show);

        initListView();

        initDateSelect();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_mainactivity_back :
                tcpSocketClient.disconnect();
                Intent intent1 = new Intent(this, loginActivity.class);
                startActivity(intent1);
                finish();
                break;
            case R.id.iv_mainactivity_close :
                AndroidExitimpl androidExitimpl = new AndroidExitimpl();
                androidExitimpl.DialogShow(myContext);
                break;
        }
    }

    private void initListView(){
        /* 注意： 一定要将String[]转成List类型，否则不能动态增加和删除Item */
        mList = new ArrayList<>(Arrays.asList(mListData));

        /* 创建适配器实例 */
        mAdapter = new ArrayAdapter<String>(mainActivity.this, android.R.layout.simple_list_item_1, mList);

        /* Sets the data behind this ListView */
        LvMainactivityShow.setAdapter(mAdapter);

        /* 设置监听 */
        LvMainactivityShow.setOnItemClickListener(new ItemSelectedListener());
    }

    private void initDateSelect(){
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        monthOfYear = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @SneakyThrows
            @Override
            public void onDateSet(DatePicker view,
                                  int year, int monthOfYear, int dayOfMonth) {
                String currentDate = String.valueOf(new StringBuilder()
                        .append(year)
                        .append((monthOfYear+1)<10 ? "0" + (monthOfYear+1) : (monthOfYear+1))
                                .append((dayOfMonth < 10) ? "0" + dayOfMonth : dayOfMonth));
                System.out.println(currentDate);
                SwitchToData(currentDate);
            }
        }, year, monthOfYear, dayOfMonth);
    }

    private String getCurrentTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH");
        Date date = new Date();
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        String currentTime = sdf.format(date);
        System.out.println(currentTime);
        return currentTime;
    }

    private void SwitchToData(String CurrentDate){
        Intent intent = new Intent(this, record_Showimpl.class);
        Bundle bundle = new Bundle();
        bundle.putString("CurrentDate",CurrentDate);
        intent.putExtra("bundle",bundle);
        startActivity(intent);
        System.out.println(CurrentDate);
        this.finish();
    }

    private class ItemSelectedListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /* 获取Item内容 */
            String itemStr = mAdapter.getItem(position);

            switch (itemStr){
                case "显示当前空气温湿度" :
                    String currentTime = getCurrentTime();
                    String[] split_result=currentTime.split(" ");
                    String sendMsg = "{\"date\":\""+split_result[0]+"\",\"time\":\""+split_result[1]+"\",\"type\":\"11\"}";
                    tcpSocketClient.send(sendMsg);
                    tcpSocketClient.receive();
                    List<Record> record= tcpSocketClient.getsplit_result();
                        AndroidAirShowimpl androidAirShowimpl = new AndroidAirShowimpl();
                        androidAirShowimpl.DialogShow(myContext, record.get(0));
                        tcpSocketClient.clear_List();
                    break;
                case "显示灌溉记录" :
                    datePickerDialog.show();
                    break;
            }
            Log.d(LOG_TAG, "onItemClick, Item: " + itemStr);
        }
    }

}


