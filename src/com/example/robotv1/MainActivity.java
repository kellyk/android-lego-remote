package com.example.robotv1;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.*;
import android.content.Intent;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		int REQUEST_ENABLE_BT = 10;
		
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter != null) {
		    // Device supports bluetooth
			if (!mBluetoothAdapter.isEnabled()) {
			    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
			else {
				TextView tv = (TextView) findViewById(R.id.ui_connect_textView2);
				tv.setText(R.string.connect_text_on);
				
				ImageView iv = (ImageView) findViewById(R.id.imageView1);
				iv.setImageResource(R.drawable.bluetooth_active);
			}	 
		}	
	}
	protected void onActivityResult (int requestCode, int resultCode, Intent data) {
		if (requestCode == 10)
		{
			if (resultCode == -1) {
				TextView tv = (TextView) findViewById(R.id.ui_connect_textView2);
				tv.setText(R.string.connect_text_on);
				
				ImageView iv = (ImageView) findViewById(R.id.imageView1);
				iv.setImageResource(R.drawable.bluetooth_active);
			}
			else {
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
