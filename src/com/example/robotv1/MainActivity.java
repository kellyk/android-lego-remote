package com.example.robotv1;

import java.util.Set;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.*;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		int REQUEST_ENABLE_BT = 10;
		
		//Create handle to bluetooth and check for device support
		final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter != null) {  //yes, bluetooth is supported
			if (!mBluetoothAdapter.isEnabled()) {
				//not enabled; prompt user to connect
			    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
			else {
				//enabled; show "Connected" text and bluetooth image
				TextView tv = (TextView) findViewById(R.id.ui_connect_textView2);
				tv.setText(R.string.connect_text_on);
				
				ImageView iv = (ImageView) findViewById(R.id.imageView1);
				iv.setImageResource(R.drawable.bluetooth_active);
			}	 
		}	
		else { //device can't handle bluetooth. disable buttons
			Button b = (Button) findViewById(R.id.ui_connect_button1);
			b.setEnabled(false);
		}
		
		//Create handle and onclick event for Connect button
		Button b = (Button) findViewById(R.id.ui_connect_button1);
		 b.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//On click, get a list of connected devices
					Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
					// If there are paired devices
					if (pairedDevices.size() > 0) {
					    // Loop through paired devices
					    for (BluetoothDevice device : pairedDevices) {
					    	TextView tv = (TextView) findViewById(R.id.ui_connect_status);
							tv.setText(device.getName() + "\n" + device.getAddress());
					    }
					}
				}

			});
	}
	//Grab the result of Start activity (So far, only activity is prompting user to connect bluetooth
	protected void onActivityResult (int requestCode, int resultCode, Intent data) {
		if (requestCode == 10) //catches result from when user was prompted to connect bluetooth
		{
			if (resultCode == -1) { //success
				TextView tv = (TextView) findViewById(R.id.ui_connect_textView2);
				tv.setText(R.string.connect_text_on);
				
				ImageView iv = (ImageView) findViewById(R.id.imageView1);
				iv.setImageResource(R.drawable.bluetooth_active);
			}
			else { //failure (or permission denied)
				TextView tv = (TextView) findViewById(R.id.ui_connect_textView2);
				tv.setText("Bluetooth denied.");
				Button b = (Button) findViewById(R.id.ui_connect_button1);
				b.setEnabled(false);
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	

}
