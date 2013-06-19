package com.design_fun.motorctrl;


import com.design_fun.motorctrl.BluetoothChatService;
import com.design_fun.motorctrl.DeviceListActivity;

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
import android.util.Log;

import java.util.ArrayList;

public class MainActivity extends Activity{
	private TextView mTview;
	private ScrollView mScroll;
	private ToggleButton mTglLed1;
	private ToggleButton mTglLed2;
	private ToggleButton mTglLed3;
	private ToggleButton mTglLed4;

    //���b�Z�[�W�萔
    public static final int MSG_STATE_CHANGE=1;
    public static final int MSG_READ        =2;
    public static final int MSG_WRITE       =3;

    //���N�G�X�g�萔
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
 
	    // Get local Bluetooth adapter
		btAdapter = BluetoothAdapter.getDefaultAdapter();
	 
	    // If the adapter is null, then Bluetooth is not supported
	    if (btAdapter == null) {
	        Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
	        finish();
	        return;
	    }

		Log.d("debug","onCreate");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
 
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

    //�I�v�V�������j���[�I�����ɌĂ΂��
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        //����
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
	
    //�A�N�e�B�r�e�B�J�n��(�X�g�b�v����̕��A)�ɌĂ΂��
    @Override
    public void onStart() {
        super.onStart();
        if (!btAdapter.isEnabled()) {
        	Intent enableIntent = new Intent(
                BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent,RQ_ENABLE_BT);
        } else {
            if (chatService==null) chatService=
                new BluetoothChatService(this,handler);
        }

        Log.d("debug","onStart");

    }

    //�A�N�e�B�r�e�B�ĊJ��(�|�[�Y����̕��A)�ɌĂ΂��
    @Override
    public synchronized void onResume() {
        super.onResume();
        if (chatService!=null) {
            if (chatService.getState()==BluetoothChatService.STATE_NONE) {
                //Bluetooth�̐ڑ��҂�(�T�[�o)
                chatService.start();
            }
        }
        Log.d("debug","onResume");
    }

    //�A�N�e�B�r�e�B�j�����ɌĂ΂��
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatService!=null) chatService.stop();
        Log.d("debug","onDestroy");
    }
    
    //�`���b�g�T�[�o��������擾����n���h��
    private final Handler handler=new Handler() {
  	  ChkResponse chkRes = new ChkResponse();

  	  
        //�n���h�����b�Z�[�W
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_STATE_CHANGE:
                switch (msg.arg1) {
                case BluetoothChatService.STATE_CONNECTED:
                    scr_append("�ڑ�����");break;
                case BluetoothChatService.STATE_CONNECTING:
                	scr_append("�ڑ���");break;
                case BluetoothChatService.STATE_LISTEN:
                case BluetoothChatService.STATE_NONE:
                	scr_append("���ڑ�");break;
                }
                break;
            //���b�Z�[�W��M
            case MSG_READ:
                byte[] readBuf=(byte[])msg.obj;
                int retCode;
                
          	    mTview.append(new String(readBuf,0,msg.arg1));
          	    
          	    retCode=chkRes.appendBuf(readBuf,msg.arg1);
          	    if(retCode!=-1){
      	    	  	String pstr = String.format(":%d", retCode);
                    scr_append(pstr);
                    chkRes.clear();
          	    }
                  

          	    break;
            
            case MSG_WRITE:
            	mTview.append(new String((byte[])msg.obj)+"...");
            	chkRes.clear();          
            }
          	      
            
        }
    };

    //�A�N�e�B�r�e�B���A���ɌĂ΂��
    public void onActivityResult(int requestCode,int resultCode,Intent data) {
        switch (requestCode) {
        //�[������
        case RQ_CONNECT_DEVICE:
            if (resultCode==Activity.RESULT_OK) {
                String address=data.getExtras().
                    getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                
                //Bluetooth�̐ڑ��v��(�N���C�A���g)
                BluetoothDevice device=btAdapter.getRemoteDevice(address);
                chatService.connect(device);
            }
//        	scr_append("RQ_CONNECT_DEVICE");
            break;
        
        //�����L��
        case RQ_ENABLE_BT:
            if (resultCode==Activity.RESULT_OK) {
                chatService=new BluetoothChatService(this,handler);
            } else {
                Toast.makeText(this,"Bluetooth���L���ł͂���܂���",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    



	public void onClickSpeed(View v)
	{
		switch(v.getId()){
		case R.id.button1:
			chatService.write("(UP:PWM1)".getBytes());
			break;
			
		case R.id.button2:
			chatService.write("(DOWN:PWM1)".getBytes());
			break;
		}
		
	}


	 public void onClickTglBtn(View v){
		 String str;
		 
		 switch(v.getId()){
		 case R.id.toggleButton1:

			if(mTglLed1.isChecked()){
				str = "(ON:LED1)";
			}
			else{
				str = "(OFF:LED1)";
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
	
	
	public class ChkResponse{
		private StringBuffer stBuf;
		private ArrayList <String> listResponse;
		private int index;
		
		public ChkResponse() {
	        stBuf = new StringBuffer();
	        listResponse = new ArrayList<String>();
	        listResponse.add("(OK)");
	        listResponse.add("(ERR)");
		}
		
		public void clear(){
			stBuf.setLength(0);
		}
		
		public int appendBuf(byte [] buf,int bytes){
			byte nbuf[] = new byte[bytes];
			for(int i=0;i<bytes;i++){
				nbuf[i]=buf[i];
			}
			
			String str = new String(nbuf);

	        stBuf.append(str);
//	    	  System.out.printf("len:%d:%d:%s\n",stBuf.length(),str.length(),stBuf.toString());
	        index=listResponse.indexOf(stBuf.toString());
	        return index;
		}
		
		public int indexResponse(){
			return index;
		}
		
		public int bytesResponse(){
			return stBuf.length();
		}
		
		public String stringResponse(){
			return stBuf.toString();
		}
	}    
  
	
	
}








  


