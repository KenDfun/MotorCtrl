package com.design_fun.motorctrl;

import com.design_fun.motorctrl.BluetoothChatService;
//import com.design_fun.motorctrl.DeviceListActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.util.Log;;

public class MainActivity extends Activity{
	private TextView mTview;
	private ScrollView mScroll;
	private ToggleButton mTglLed1;
	private ToggleButton mTglLed2;
	private ToggleButton mTglLed3;
	private ToggleButton mTglLed4;

    //メッセージ定数
    public static final int MSG_STATE_CHANGE=1;
    public static final int MSG_READ        =2;

    //リクエスト定数
    private static final int RQ_CONNECT_DEVICE=1;
    private static final int RQ_ENABLE_BT     =2;

	
    //Bluetooth
    private BluetoothAdapter     btAdapter;
    private BluetoothChatService chatService;
    
	//btSp = new this.ButtonSp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mScroll = (ScrollView)findViewById(R.id.scrollView1);
		mTview = (TextView)findViewById(R.id.textView1);

		
		mTglLed1 = (ToggleButton) findViewById(R.id.toggleButton1);  
//		mTglLed1.setOnCheckedChangeListener((OnCheckedChangeListener) this);
		mTglLed2 = (ToggleButton) findViewById(R.id.toggleButton2);  
		mTglLed3 = (ToggleButton) findViewById(R.id.toggleButton3);  
		mTglLed4 = (ToggleButton) findViewById(R.id.toggleButton4);  
        Log.d("debug","onCreate");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
 
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

    //オプションメニュー選択時に呼ばれる
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        //検索
        case R.id.search_bt:
            Intent serverIntent=new Intent(this,DeviceListActivity.class);
            startActivityForResult(serverIntent,RQ_CONNECT_DEVICE);
            return true;
        case R.id.menu_settings:
//          Intent serverIntent=new Intent(this,DeviceListActivity.class);
//          startActivityForResult(serverIntent,RQ_CONNECT_DEVICE);
      	scr_append("menu");
      		return true;
      	}
        return false;
    }
	
    //アクティビティ開始時(ストップからの復帰)に呼ばれる
    @Override
    public void onStart() {
        super.onStart();
/*
        if (!btAdapter.isEnabled()) {
            Intent enableIntent = new Intent(
                BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent,RQ_ENABLE_BT);
        } else {
            if (chatService==null) chatService=
                new BluetoothChatService(this,handler);
        }
*/
        if (chatService==null) chatService=
                new BluetoothChatService(this,handler);
        Log.d("debug","onStart");

    }

    //アクティビティ再開時(ポーズからの復帰)に呼ばれる
    @Override
    public synchronized void onResume() {
        super.onResume();
        if (chatService!=null) {
            if (chatService.getState()==BluetoothChatService.STATE_NONE) {
                //Bluetoothの接続待ち(サーバ)
                chatService.start();
            }
        }
        Log.d("debug","onResume");
    }

    //アクティビティ破棄時に呼ばれる
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatService!=null) chatService.stop();
        Log.d("debug","onDestroy");
    }

    /*
    //他のBluetooth端末からの発見を有効化(4)
    private void ensureDiscoverable() {
        if (btAdapter.getScanMode()!=
            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent=new Intent(
                BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(
                BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,300);
            startActivity(discoverableIntent);
        }
    }
*/
    
    //チャットサーバから情報を取得するハンドラ
    private final Handler handler=new Handler() {
        //ハンドルメッセージ
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_STATE_CHANGE:
                switch (msg.arg1) {
                case BluetoothChatService.STATE_CONNECTED:
                    scr_append("接続完了");break;
                case BluetoothChatService.STATE_CONNECTING:
                	scr_append("接続中");break;
                case BluetoothChatService.STATE_LISTEN:
                case BluetoothChatService.STATE_NONE:
                	scr_append("未接続");break;
                }
                break;
            //メッセージ受信
            case MSG_READ:
                byte[] readBuf=(byte[])msg.obj;
                scr_append(new String(readBuf,0,msg.arg1));
                break;
            }
        }
    };

    //アクティビティ復帰時に呼ばれる
    public void onActivityResult(int requestCode,int resultCode,Intent data) {
        switch (requestCode) {
        //端末検索
        case RQ_CONNECT_DEVICE:
        	/*
            if (resultCode==Activity.RESULT_OK) {
                String address=data.getExtras().
                    getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                
                //Bluetoothの接続要求(クライアント)
                BluetoothDevice device=btAdapter.getRemoteDevice(address);
                chatService.connect(device);
            }
            */
        	scr_append("RQ_CONNECT_DEVICE");
            break;
        //発見有効
        case RQ_ENABLE_BT:
        	/*
            if (resultCode==Activity.RESULT_OK) {
                chatService=new BluetoothChatService(this,handler);
            } else {
                Toast.makeText(this,"Bluetoothが有効ではありません",
                	Toast.LENGTH_SHORT).show();
                finish();
            }
            */
        	scr_append("RQ_ENABLE_BT");      	
        }
    }


/*
    //受信テキストの追加
    private void addText(final String text) {
        //ハンドラによるユーザーインタフェース操作
        handler.post(new Runnable(){
            public void run() {
                lblReceive.setText(text+"\n"+
                    lblReceive.getText());
            }
        });
    }
    
    //ボタンクリックイベントの処理
    public void onClick(View v) {
        if (v==btnSend) {
            try {
                //メッセージの送信
                String message=edtSend.getText().toString();
                if (message.length()>0) {
                    chatService.write(message.getBytes());
                }
                addText(message);
                edtSend.setText("",TextView.BufferType.NORMAL);
            } catch (Exception e) {
                addText("通信失敗しました");
            }           
        }
    }  
}
*/
	

	public void onClickSpeed(View v)
	{
		switch(v.getId()){
		case R.id.button1:
			chatService.write("Push Up".getBytes());
			break;
			
		case R.id.button2:
			chatService.write("Push Down".getBytes());
			break;
		}
		
	}


	 public void onClickTglBtn(View v){
		 String str;
		 
		 switch(v.getId()){
		 case R.id.toggleButton1:
			str = "LED1 ";
			if(mTglLed1.isChecked()){
				str += "ON";
			}
			else{
				str += "OFF";
			}
			break;
		 case R.id.toggleButton2:
			str = "LED2 ";
			if(mTglLed2.isChecked()){
				str += "ON";
			}
			else{
				str += "OFF";
			}
			break;
		 case R.id.toggleButton3:
			str = "LED3 ";
			if(mTglLed3.isChecked()){
				str += "ON";
			}
			else{
				str += "OFF";
			}
			break;
		 case R.id.toggleButton4:
			str = "LED4 ";
			if(mTglLed4.isChecked()){
				str += "ON";
			}
			else{
				str += "OFF";
			}
			break;
		default:
			str = "none";
		} 		
		
		chatService.write(str.getBytes());
	 }

	 private void scr_append(String str){
		 mTview.append(str+"\n> ");
		 mScroll.post(new ScrollDown());
	 }
	 
	/* scroll down thread */
	private class ScrollDown implements Runnable { // (8)
        @Override
		public void run() {
        	mScroll.fullScroll(View.FOCUS_DOWN); // (9)
        }
    }	
	
}

