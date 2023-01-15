package com.appframes;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	
Button btnHDLC,btnPPP, btnEthernet;
String ptl = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		btnHDLC = (Button) findViewById(R.id.buttonHDLC);
		btnPPP = (Button) findViewById(R.id.buttonPPP);
		btnEthernet = (Button) findViewById(R.id.buttonEthernet);
		btnHDLC.setOnClickListener(this);
		btnPPP.setOnClickListener(this);
		btnEthernet.setOnClickListener(this);
		
	}


	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
		
		case R.id.buttonHDLC:
			ptl = "HDLC";
			ejecutar_Trama();
			break;
		case R.id.buttonPPP:
			ptl = "PPP";
			ejecutar_Trama();
			break;
		case R.id.buttonEthernet:
			ptl = "Ethernet";
			ejecutar_Trama();
			break;
		}
		
	}
	
	public void ejecutar_Trama(){
		
		Bundle sendData = new Bundle();
		sendData.putString("keyDatos",ptl);
		
		Intent inte= new Intent(MainActivity.this,  Frames.class);
		inte.putExtras(sendData);
		startActivity(inte);
	}
	

}
