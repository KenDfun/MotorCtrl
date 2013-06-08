package com.design_fun.motorctrl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class BluetoothChatService {
    //設定定数
    private static final String NAME="Moterctrl";
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
    private AcceptThread     acceptThread;
    private ConnectThread    connectThread;
    private ConnectedThread  connectedThread;
    private int              state;

    //コンストラクタ
    public BluetoothChatService(Context context,Handler handler) {
        this.adapter=BluetoothAdapter.getDefaultAdapter();
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
        if (connectThread!=null) {
            connectThread.cancel();connectThread=null;}
        if (connectedThread!=null) {
            connectedThread.cancel();connectedThread=null;}
        if (acceptThread==null) {
            acceptThread=new AcceptThread();
            acceptThread.start();
        }
        setState(STATE_LISTEN);
    }

    //Bluetoothの接続要求(クライアント)
    public synchronized void connect(BluetoothDevice device) {
        if (state==STATE_CONNECTING) {
            if (connectThread!=null) {
                connectThread.cancel();connectThread=null;}
        }
        if (connectedThread!=null) {
            connectedThread.cancel();connectedThread=null;}
        connectThread=new ConnectThread(device);
        connectThread.start();
        setState(STATE_CONNECTING);
    }

    //Bluetooth接続完了後の処理
    public synchronized void connected(BluetoothSocket socket,
        BluetoothDevice device) {
        if (connectThread!=null) {
            connectThread.cancel();connectThread=null;}
        if (connectedThread!=null) {
            connectedThread.cancel();connectedThread=null;}
        if (acceptThread!=null) {
            acceptThread.cancel();acceptThread=null;}
        connectedThread=new ConnectedThread(socket);
        connectedThread.start();
        setState(STATE_CONNECTED);
    }

    //Bluetoothの切断
    public synchronized void stop() {
        if (connectThread!=null) {
            connectThread.cancel();connectThread=null;}
        if (connectedThread!=null) {
            connectedThread.cancel();connectedThread=null;}
        if (acceptThread!=null) {
            acceptThread.cancel();acceptThread=null;}
        setState(STATE_NONE);
        Log.d("debug","chatservice stop");
    }
 

    //書き込み
    public void write(byte[] out) {
        ConnectedThread r;
        
        synchronized (this) {
            if (state!=STATE_CONNECTED) return;
            r=connectedThread;
        }
        r.write(out);
    }

    
    //Bluetoothの接続待ち(サーバ)(5)
    private class AcceptThread extends Thread {
        private BluetoothServerSocket serverSocket;
        
        //コンストラクタ
        public AcceptThread() {
            try {
                serverSocket=adapter.listenUsingRfcommWithServiceRecord(NAME,MY_UUID);
            } catch (IOException e) {
            }
        }

        //処理
        public void run() {
            BluetoothSocket socket=null;
            while (state!=STATE_CONNECTED) {
                try {
                    socket=serverSocket.accept();
                } catch (IOException e) {
                    break;
                }
                if (socket!=null) {
                    synchronized (BluetoothChatService.this) {
                        switch (state) {
                        case STATE_LISTEN:
                        case STATE_CONNECTING:
                            connected(socket,socket.getRemoteDevice());
                            break;
                        case STATE_NONE:
                        case STATE_CONNECTED:
                            try {
                                socket.close();
                            } catch (IOException e) {
                            }
                            break;
                        }
                    }
                }
            }
        }

        //キャンセル
        public void cancel() {
            try {
                serverSocket.close();
            } catch (IOException e) {
            }
        }
    }

    //Bluetoothの接続要求(クライアント)(6)
    private class ConnectThread extends Thread {
        private BluetoothDevice device;
        private BluetoothSocket socket;

        //コンストラクタ
        public ConnectThread(BluetoothDevice device) {
            try {
                this.device=device;
                this.socket=device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
            }
        }

        //処理
        public void run() {
            adapter.cancelDiscovery();
            try {
                socket.connect();
            } catch (IOException e) {
                setState(STATE_LISTEN);
                try {
                    socket.close();
                } catch (IOException e2) {
                }
                BluetoothChatService.this.start();
                return;
            }
            synchronized (BluetoothChatService.this) {
                connectThread=null;
            }
            connected(socket,device);
        }

        //キャンセル
        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }

    //Bluetooth接続完了後の処理(7)
    private class ConnectedThread extends Thread {
        private BluetoothSocket socket;
        private InputStream     input;
        private OutputStream    output;

        //コンストラクタ
        public ConnectedThread(BluetoothSocket socket) {
            try {
                this.socket=socket;
                this.input =socket.getInputStream();
                this.output=socket.getOutputStream();
            } catch (IOException e) {
            }
        }

        //処理
        public void run() {
            byte[] buf=new byte[10];
            int bytes;

            
            while (true) {
                try {
                    bytes=input.read(buf);

                } catch (IOException e) {
                    setState(STATE_LISTEN);
                    break;
                }
            	handler.obtainMessage(MainActivity.MSG_READ,bytes,-1,buf).sendToTarget();

            }
        }

        //書き込み
        public void write(byte[] buf) {
            try {
                output.write(buf);
            } catch (IOException e) {
            }

            handler.obtainMessage(MainActivity.MSG_WRITE,-1,-1,buf).sendToTarget();

        }

        //キャンセル
        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }
    

    
}
