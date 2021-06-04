package com.example.intelligentirrigation;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.example.intelligentirrigation.entity.Record;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static android.content.ContentValues.TAG;

public class TCPSocketClient extends Exception{
    private static TCPSocketClient tcpSocketClient;
    private final Map<String,Object>
            Task_Map = new ConcurrentHashMap<>();
    //socket and thread
    private Socket socket;
    private Thread connectThread,send_dataThread,receive_dataThread;
    private String ipAddress;
    private int port;
    //stream and buffer
    private PrintWriter printWriter;
    private OutputStream outputStream;
    private InputStream inputStream;
    private InputStreamReader inputStreamReader;
    private BufferedReader bufferedReader;

    List<Record> recordList = new ArrayList<>();

    //    提供一个全局的静态方法
    public static TCPSocketClient sharedCenter() {
        if (tcpSocketClient == null) {
            synchronized (TCPSocketClient.class) {
                if (tcpSocketClient == null) {
                    tcpSocketClient = new TCPSocketClient();
                }
            }
        }
        return tcpSocketClient;
    }

    public void connect(String ipAddress,int port) throws IOException {
        connectThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // 建立tcp服务
                try {
                    socket = new Socket(ipAddress, port);
                    if (isConnected()) {
                        TCPSocketClient.sharedCenter().ipAddress = ipAddress;
                        TCPSocketClient.sharedCenter().port = port;
                        outputStream = socket.getOutputStream();
                        inputStream = socket.getInputStream();
                        Log.i(TAG,"连接成功");
                    }else {
                        Log.i(TAG,"连接失败");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG,"连接异常");
                }
            }
        });
        connectThread.start();
        while (connectThread.isAlive()){}
        System.out.println("ClientHandler Active");
        Task_Map.put("match","false");
        Task_Map.put("save","false");

    }

    /**
     * 判断是否连接
     */
    public boolean isConnected() {
        return socket.isConnected();
    }
    /**
     * 连接
     */
    public void connect() throws IOException {
        connect(ipAddress,port);
    }
    /**
     * 断开连接
     */
    public void disconnect() {
        if (isConnected()) {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                socket.close();
                if (socket.isClosed()) {
                    System.out.println("已经断开");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送数据
     *
     * @param data  数据
     */
    public void send(String data) {
        send_dataThread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (socket != null) {
                    printWriter=new PrintWriter(outputStream);
                    printWriter.print(data);
                    printWriter.flush();
                    Log.i(TAG,"发送成功");
                } else {
                    try {
                        connect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        send_dataThread.start();
        while (send_dataThread.isAlive()){}
    }


    /**
     * 接收数据
     */
    public void receive() {
        receive_dataThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    /**得到的是16进制数，需要进行解析*/
                    byte[] bt = new byte[8192];
//                获取接收到的字节和字节数
                    int length = inputStream.read(bt);
//                获取正确的字节
                    byte[] bs = new byte[length];
                    System.arraycopy(bt, 0, bs, 0, length);
                    String str = new String(bs, "UTF-8");
                    System.out.println(str);
                    String[] split_result = str.split("x");
                    Map<String,Object> ReceiveData = JSON.parseObject(split_result[0]);
                    String type = (String) ReceiveData.get("type");

                    switch (type){
                        case "11" :
                            System.out.println(ReceiveData);
                            for (int i=0;i<split_result.length;i++){
                                Map<String,Object> ShowData = JSON.parseObject(split_result[i]);
                                Record record = new Record();
                                record.setTemperature((String) ShowData.get("temperature"));
                                record.setHumidity((String) ShowData.get("humidity"));
                                record.setCurrentTime((String) ShowData.get("currentTime"));
                                record.setRemark((String) ShowData.get("remark"));
                                if (record.getCurrentTime()!=null){
                                    recordList.add(record);
                                }else if (record.getTemperature()!=null){
                                    recordList.add(record);
                                }else {
                                    recordList.add(record);
                                }
                            }
                            break;
                        case "00" :
                            String UsercheckResult = (String) ReceiveData.get("CheckResult");
                            if (UsercheckResult.equals("OK")){
                                System.out.println("match = true");
                                Task_Map.put("match","true");
                            }
                            break;
                        case "10" :
                            String UserSaveResult = (String) ReceiveData.get("SaveResult");
                            if (UserSaveResult.equals("OK")){
                                System.out.println("save = true");
                                Task_Map.put("save","true");
                            }
                            break;
                    }
                    Log.i(TAG, "接收成功");
                } catch (IOException e) {
                    Log.i(TAG, "接收失败");
                }
            }
        });
        receive_dataThread.start();
        while (receive_dataThread.isAlive()){}
    }

    public void setLoginTask_Map(String match){
        Task_Map.put("match",match);
    }
    public String getLoginTask_Map(){
        return (String) Task_Map.get("match");
    }

    public void setRegisterTask_Map(String save){
        Task_Map.put("save",save);
    }
    public String getRegistTask_Map(){
        return (String) Task_Map.get("save");
    }

    public boolean Socket_state(){
        return socket!=null;
    }

    public List<Record> getsplit_result(){
        return this.recordList;
    }

    public void clear_List(){
        this.recordList.clear();
    }
}

