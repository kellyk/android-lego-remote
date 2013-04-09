package com.example.robotv1;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.*;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnTouchListener{

    private BluetoothAdapter mBluetoothAdapter; 
    final String tag = "BtTry";
	final String ROBOTNAME = "Steve";
	private final UUID SP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	int REQUEST_ENABLE_BT = 1;

	// BT Variables
	private Set<BluetoothDevice> pairedDevices;
	private BluetoothSocket socket;
	private BluetoothDevice bd;
	private InputStream is = null;
	private OutputStream os = null;
	private List<BluetoothDevice>  deviceNames;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//set on touch listeners for forward, backward, left and right buttons
		ImageButton forward = (ImageButton) findViewById(R.id.imageButtonForward);
		forward.setOnTouchListener(this);
		ImageButton backward = (ImageButton) findViewById(R.id.imageButtonBackward);
		backward.setOnTouchListener(this);
		ImageButton left = (ImageButton) findViewById(R.id.imageButtonLeft);
		left.setOnTouchListener(this);
		ImageButton right = (ImageButton) findViewById(R.id.buttonRight);
		right.setOnTouchListener(this);

		//Create handle to bluetooth and check for device support
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter != null) {  //yes, bluetooth is supported
			if (!mBluetoothAdapter.isEnabled()) {
				//bluetooth is off; prompt user to connect
			    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
			else {
				//bluetooth is on; show "connected" text and bluetooth image
				TextView connectedText = (TextView) findViewById(R.id.ui_connect_textView2);
				connectedText.setText(R.string.connect_text_on);

				ImageView bluetoothImage = (ImageView) findViewById(R.id.imageView1);
				bluetoothImage.setImageResource(R.drawable.bluetooth_active);
			}	 
		}	
		else { //device can't handle bluetooth. disable buttons
			Button connectButton = (Button) findViewById(R.id.ui_connect_button1);
			connectButton.setEnabled(false);
		}

		//Create handle and onclick event for Connect button
		Button connectButton = (Button) findViewById(R.id.ui_connect_button1);
		 connectButton.setOnClickListener(new OnClickListener() {
		 	@Override
			public void onClick(View v) {
				connectNXT();
			}
		});
	}

	//Grab the result of Start activity (So far, only activity is prompting user to connect bluetooth
	protected void onActivityResult (int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) //catches result from when user was prompted to connect bluetooth
		{
			if (resultCode == -1) { //success
				TextView tv = (TextView) findViewById(R.id.ui_connect_textView2);
				tv.setText(R.string.connect_text_on);

				ImageView bluetoothImage = (ImageView) findViewById(R.id.imageView1);
				bluetoothImage.setImageResource(R.drawable.bluetooth_active);
			}
			else { //failure (or permission denied)
				TextView connectedText = (TextView) findViewById(R.id.ui_connect_textView2);
				connectedText.setText("Bluetooth denied.");
				Button connectButton = (Button) findViewById(R.id.ui_connect_button1);
				connectButton.setEnabled(false);
			}
		}
	}

	//Set on touch listeners for arrow buttons
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int action = event.getAction();
		int buttonID = v.getId();
		 switch(action)
		 {
		 	case MotionEvent.ACTION_DOWN:
		 		switch(buttonID)
		 		{
			 		case R.id.imageButtonForward:
			 			forward(v);
			 			break;
			 		case R.id.imageButtonBackward:
			 			backward(v);
			 			break;
			 		case R.id.imageButtonLeft:
			 			left(v);
			 			break;
			 		case R.id.buttonRight:
			 			right(v);
			 			break;
			 		default:
			 			break;
		 		}
		 		break;
		 	case MotionEvent.ACTION_UP:
			    stopNXT(v);
			    break;
		 	default:
		 		break;
		 }
		 if(action >= 0)
			 return true;
		 else
			 return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void connectNXT() {
		try	{
			//On click, get a list of connected devices
			pairedDevices = mBluetoothAdapter.getBondedDevices();
			// If there are paired devices
			if (pairedDevices.size() > 0) {
			    // Loop through paired devices
				Iterator<BluetoothDevice> it = pairedDevices.iterator();
	    		while (it.hasNext()) {
	       			bd = it.next();
	    			Log.i(tag,"Name of peer is [" + bd.getName() + "]");
	    			try {
	    				deviceNames.add(bd);
		    			Log.i(tag,"Item was added to list");
	    			}
	    			catch (Exception e){
		    			Log.i(tag,"No idea what is wrong with list");
	    			}
	    			if (bd.getName().equalsIgnoreCase(ROBOTNAME)) {
	    				Log.i(tag, "Found "+ bd.getName() + " with ID " + bd.getAddress());
	    				Log.i(tag,bd.getBluetoothClass().toString());
	    				try {	
	    					socket = bd.createRfcommSocketToServiceRecord(SP_UUID);
	    				//	socket = bd.createRfcommSocketToServiceRecord(java.util.UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
	    					socket.connect();
	    				}
	    				catch (Exception e) {
	    					Log.e(tag,"Error interacting with remote device -> " + e.getMessage()); 
	    				}

	        			try {
	        				is = socket.getInputStream();
	        				os = socket.getOutputStream();
	        			} catch (Exception e) {
	        				is = null;
	        				os = null;
	        				disconnectNXT(null);
	        			}
	    			}
    			}
				return;
			}
		} 
		catch (Exception e) 	{
			Log.e(tag,"Failed in finding NXT -> " + e.getMessage());
		}
	}

	public void disconnectNXT(View v) {
		try {
			Log.i(tag,"Attempting to break BT connection of " + bd.getName());
			socket.close();
			is.close();
			os.close();
			Log.i(tag, "BT connection of " + bd.getName() + " is disconnected");
		}
		catch (Exception e)	{
			Log.e(tag,"Error in disconnect -> " + e.getMessage());
		}
	}

	public void stopNXT(View v) {
		MoveMotor(0, 0, 0x00);
		MoveMotor(1, 75, 0x00);
		MoveMotor(2, 75, 0x00);
	}

	public void forward(View v) {
		MoveMotor(1, 75, 0x20);
		MoveMotor(2, 75, 0x20);
	}

	public void backward(View v) {
		MoveMotor(1, -75, 0x20);
		MoveMotor(2, -75, 0x20);
	}

	public void left(View v) {
		MoveMotor(1, 75, 0x20);
	}

	public void right(View v) {
		MoveMotor(2, 75, 0x20);
	}

	private void MoveMotor(int motor,int speed, int state) {
		try {
			Log.i(tag,"Attempting to move [" + motor + " @ " + speed + "]");

			byte[] buffer = new byte[15];

			buffer[0] = (byte) (15-2);			//length lsb
			buffer[1] = 0;						// length msb
			buffer[2] =  0;						// direct command (with response)
			buffer[3] = 0x04;					// set output state
			buffer[4] = (byte) motor;			// output 1 (motor B)
			buffer[5] = (byte) speed;			// power
			buffer[6] = 1 + 2;					// motor on + brake between PWM
			buffer[7] = 0;						// regulation
			buffer[8] = 0;						// turn ration??
			buffer[9] = (byte) state; //0x20;					// run state
			buffer[10] = 0;
			buffer[11] = 0;
			buffer[12] = 0;
			buffer[13] = 0;
			buffer[14] = 0;

			os.write(buffer);
			os.flush();

		}
		catch (Exception e) {
			Log.e(tag,"Error in MoveForward(" + e.getMessage() + ")");
		}		
	}

}
