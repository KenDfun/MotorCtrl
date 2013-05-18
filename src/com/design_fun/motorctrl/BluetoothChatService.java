package com.design_fun.motorctrl;

import java.util.UUID;

import com.design_fun.motorctrl.MainActivity;
//import com.example.bluetoothex.BluetoothEx;
//import com.example.bluetoothex.BluetoothChatService.ConnectedThread;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Handler;

public class BluetoothChatService {
    //設定定数
    private static final String NAME="BluetoothEx";
    private static final UUID   MY_UUID=
//        UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
          UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");  
    
    //状態定数
    public static final int STATE_NONE      =0;
    public static final int STATE_LISTEN    =1;
    public static final int STATE_CONNECTING=2;
    public static final int STATE_CONNECTED =3;

    //変数
    private BluetoothAdapter adapter;
    private Handler          handler;
//    private AcceptThread     acceptThread;
//    private ConnectThread    connectThread;
//    private ConnectedThread  connectedThread;
    private int              state;

    //コンストラクタ
    public BluetoothChatService(Context context,Handler handler) {
//        this.adapter=BluetoothAdapter.getDefaultAdapter();
        this.state  =STATE_NONE;
        this.handler=handler;
    }
    
    
    //状態の指定
    private synchronized void setState(int state) {
        this.state=state;
        handler.obtainMessage(
            MainActivity.MSG_STATE_CHANGE,state,-1).sendToTarget();
    }
    
    //状態の取得
    public synchronized int getState() {
        return state;
    }
    
    //Bluetoothの接続待ち(サーバ)
    public synchronized void start() {
        setState(STATE_LISTEN);
    }

    //Bluetoothの切断
    public synchronized void stop() {
/*
        if (connectThread!=null) {
            connectThread.cancel();connectThread=null;}
        if (connectedThread!=null) {
            connectedThread.cancel();connectedThread=null;}
        if (acceptThread!=null) {
            acceptThread.cancel();acceptThread=null;}
*/
        setState(STATE_NONE);
    }
    
    //書き込み
    public void write(byte[] buf) {
    	int bytes = buf.length;
    	
        handler.obtainMessage(MainActivity.MSG_READ,
                bytes,-1,buf).sendToTarget();
        /*
        ConnectedThread r;
        synchronized (this) {
            if (state!=STATE_CONNECTED) return;
            r=connectedThread;
        }
        r.write(out);
        */
    }

    
}
