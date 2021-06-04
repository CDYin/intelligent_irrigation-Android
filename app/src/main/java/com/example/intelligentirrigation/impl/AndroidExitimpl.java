package com.example.intelligentirrigation.impl;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.intelligentirrigation.R;
import com.example.intelligentirrigation.TCPSocketClient;

public class AndroidExitimpl {

    private TCPSocketClient instance = TCPSocketClient.sharedCenter();

    public void DialogShow(Context myContext){
        AlertDialog.Builder builder = new AlertDialog.Builder(myContext);
        LayoutInflater inflater = LayoutInflater.from(myContext);
        View view = inflater.inflate(R.layout.exit_manage_dialog, null);

        TextView tvExitDialogContent = view.findViewById(R.id.tv_exitdialog_content);
        Button btExitDialogCancel = view.findViewById(R.id.bt_exitdialog_cancel);
        Button btExitDialogSure = view.findViewById(R.id.bt_exitdialog_sure);
        //builer.setView(v);
        // 这里如果使用builer.setView(v)，自定义布局只会覆盖title和button之间的那部分
        final Dialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setContentView(view);//自定义布局应该在这里添加，要在dialog.show()的后面
        //dialog.getWindow().setGravity(Gravity.CENTER);//可以设置显示的位置
        btExitDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btExitDialogSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (instance.Socket_state()) {
                    instance.disconnect();
                    dialog.dismiss();
                    System.exit(0);
                }else {
                    dialog.dismiss();
                    System.exit(0);
                }

            }
        });
    }
}
