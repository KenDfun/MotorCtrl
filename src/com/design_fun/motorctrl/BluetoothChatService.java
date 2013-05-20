package com.design_fun.motorctrl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import com.design_fun.motorctrl.MainActivity;
//import com.example.bluetoothex.BluetoothEx;
//import com.example.bluetoothex.BluetoothChatService.ConnectedThread;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class BluetoothChatService {
    //�ݒ�萔
    private static final String NAME="BluetoothEx";
    private static final UUID   MY_UUID=
//        UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
          UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");  
    
    //��Ԓ萔
    public static final int STATE_NONE      =0;
    public static final int STATE_LISTEN    =1;
    public static final int STATE_CONNECTING=2;
    public static final int STATE_CONNECTED =3;

    //�ϐ�
    private BluetoothAdapter adapter;
    private Handler          handler;
//    private AcceptThread     acceptThread;
//    private ConnectThread    connectThread;
    private ConnectedThread  connectedThread;
    private int              state;

    //�R���X�g���N�^
    public BluetoothChatService(Context context,Handler handler) {
//        this.adapter=BluetoothAdapter.getDefaultAdapter();
        this.state  =STATE_NONE;
        this.handler=handler;
    }
    
    
    //��Ԃ̎w��
    private synchronized void setState(int state) {
        this.state=state;
        handler.obtainMessage(
            MainActivity.MSG_STATE_CHANGE,state,-1).sendToTarget();
    }
    
    //��Ԃ̎擾
    public synchronized int getState() {
        return state;
    }
    
    //Bluetooth�̐ڑ��҂�(�T�[�o)
    public synchronized void start() {
        setState(STATE_LISTEN);
        connected();
    }

    //Bluetooth�̐ؒf
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
        Log.d("debug","chatservice stop");
    }
 
    
    //Bluetooth�ڑ�������̏���
//    public synchronized void connected(BluetoothSocket socket,BluetoothDevice device) {
    public synchronized void connected() {
/*
    if (connectThread!=null) {
            connectThread.cancel();connectThread=null;}
        if (connectedThread!=null) {
            connectedThread.cancel();connectedThread=null;}
        if (acceptThread!=null) {
            acceptThread.cancel();acceptThread=null;}
*/
        connectedThread=new ConnectedThread();
        connectedThread.start();
        setState(STATE_CONNECTED);
    }

    //��������
    public void write(byte[] out) {
        ConnectedThread r;
        
        synchronized (this) {
            if (state!=STATE_CONNECTED) return;
            r=connectedThread;
        }
        r.write(out);
    }

    
    //Bluetooth�ڑ�������̏���(7)
    private class ConnectedThread extends Thread {
     	boolean  read_flg = false;
    	byte[] readbuf=new byte[1024];

  	
        //����
        public void run() {
            while (true) {
            	if(read_flg){
                    handler.obtainMessage(MainActivity.MSG_READ,
                            readbuf.length,-1,readbuf).sendToTarget();         		
            	}
            	read_flg = false;
            	try{
            		Thread.sleep(1000); //3000�~���bSleep����
            	}catch(InterruptedException e){}
            }
        }
        
        public void write(byte[] buf) {
        	for(int i=0; i<buf.length; i++){
        		readbuf[i] = buf[i];
        	}
        	readbuf[buf.length]='\0';
        	readbuf[buf.length+1]='\0';
        	read_flg = true;
        }

    }
    
}
