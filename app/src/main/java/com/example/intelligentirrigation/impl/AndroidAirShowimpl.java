package com.example.intelligentirrigation.impl;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.intelligentirrigation.R;
import com.example.intelligentirrigation.entity.Record;

public class AndroidAirShowimpl {
    public void DialogShow(Context myContext, Record record){
        AlertDialog.Builder builder = new AlertDialog.Builder(myContext);
        LayoutInflater inflater = LayoutInflater.from(myContext);
        View view = inflater.inflate(R.layout.air_show, null);

        TextView tvAirShowDialogContent = view.findViewById(R.id.tv_airShowdialog_content);
        Button btAirShowDialogCancel = view.findViewById(R.id.bt_airShowdialog_Confirm);

        final Dialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setContentView(view);//自定义布局应该在这里添加，要在dialog.show()的后面
        //dialog.getWindow().setGravity(Gravity.CENTER);//可以设置显示的位置

        tvAirShowDialogContent.append("温度："+record.getTemperature()+"湿度："+record.getHumidity());

        btAirShowDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}
