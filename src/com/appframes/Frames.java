package com.appframes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


import android.app.Activity;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.*;

public class Frames extends Activity implements OnClickListener{

	TextView textTitle, textFrame;
	EditText textAdress, textAdressS, textControl, textPayload;
	Button btnGenerate, btnClean;
	String cadFrame = "", protocol="";
	Spinner options; 
	Map<String, String> map = new HashMap<String, String>();
	char [] charHexa = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_frame);
		
		
		Bundle getData = getIntent().getExtras();
		protocol = getData.getString("keyDatos");
		Toast.makeText(getApplicationContext(),protocol, Toast.LENGTH_LONG).show();
		initComponents();
		
		
		switch(protocol){
			case "HDLC":
				textTitle.setText("Frame HDLC");
				options.setVisibility(View.GONE);
				textAdressS.setVisibility(View.GONE);
				break;
			case "PPP":
				textTitle.setText("Frame PPP");
				textAdress.setVisibility(View.GONE);
				textControl.setVisibility(View.GONE);
				textAdressS.setVisibility(View.GONE);
				break;
			case "Ethernet":
				textTitle.setText("Frame Ethernet");
				InputFilter[] FilterArray = new InputFilter[1];
			    FilterArray[0] = new InputFilter.LengthFilter(12);
			    textAdress.setFilters(FilterArray);
				textAdress.setHint("adress destination");
				textControl.setVisibility(View.GONE);
				break;
		}
	   
	}


	@Override
	public void onClick(View v){
		
		if(v.getId() == btnGenerate.getId())
			selectProtocol();
		if(v.getId() == btnClean.getId())
			cleanInputs();

	}
	
	
	public void selectProtocol(){
		switch(protocol){
		case "HDLC":
			
			if (validateHDLC()){
				protocolHDLC();
				btnGenerate.setEnabled(false);
			}else
				error();
			
			break;
		
		case "PPP":
			
			if(validatePPP()){
				protocolPPP();
				btnGenerate.setEnabled(false);
			}else
				error();
			break;
			
		case "Ethernet":
			if(validateEthernet()){
				protocolEthernet();
				btnGenerate.setEnabled(false);	
			}else
				error();
			break;
		}
		
		
	}
	
	//Protocolos
	public void protocolHDLC(){
		addFlag("7E");
		addAdress(textAdress.getText().toString().toUpperCase());
		addControl(textControl.getText().toString().toUpperCase());
		cadFrame += " " + text2Hexa(textPayload.getText().toString());
		cadFrame += " " + generateRandom(4);
		addFlag("7E");
		textFrame.setText(cadFrame);
	}
	
	public void protocolPPP(){
		String selected = options.getSelectedItem().toString();
			addFlag("7E");
			cadFrame += " "+ "FF";
			cadFrame += " " + "03";
			cadFrame += " "+ map.get(selected);
			cadFrame += " " + text2Hexa(textPayload.getText().toString());
			cadFrame += " " + generateRandom(4);
			addFlag("7E");
			textFrame.setText(cadFrame);
	}
	
	public void protocolEthernet(){
		String selected = options.getSelectedItem().toString();
		cadFrame += "AAAAAAAAAAAAAA";
		addSOF("AB");
		addAdress(textAdress.getText().toString().toUpperCase());
		addAdress(textAdressS.getText().toString().toUpperCase());
		cadFrame += " "+ map.get(selected);
		String payload = text2Hexa(textPayload.getText().toString());
		cadFrame += " " + stuffed(payload);
		cadFrame += " " + generateRandom(8);
		textFrame.setText(cadFrame);		
	}
	

	//Validaciones
	public boolean validateHDLC(){
		Boolean validate = false;
		String a = textAdress.getText().toString();
		String c = textControl.getText().toString();
		String p = textPayload.getText().toString();
		if(a.isEmpty()  || c.isEmpty() || p.isEmpty() ){
			validate = false;
		}else{
			if(validateLength(a, 2) && validateLength(c, 2)){
				validate = true;
			}
		}
					
		return validate;
	}
	
	public boolean validatePPP(){
		Boolean validate = false;
		if(options.getSelectedItemPosition() != 0 && !isEmptyPayload()){
				validate = true;
		}
		return validate;
	}
	
	public boolean validateEthernet(){
		Boolean validate = false;
		String d = textAdress.getText().toString();
		String o = textAdressS.getText().toString();
		if(options.getSelectedItemPosition() == 0 || d.isEmpty() || o.isEmpty() || isEmptyPayload() ){
			validate = false;
		}else{
			if(validateLength(d, 12) && validateLength(o, 12) && !d.equals(o)){
					validate = true;
			}
		}
		return validate;
	}
	
	public boolean validateLength(String cadena, int len){
		Boolean validate = false;
		if(cadena.length() == len){
			validate = true;
		}
		return validate;
	}
	
	
	public boolean isEmptyPayload(){
		boolean validate = false;
		if(textPayload.getText().toString().isEmpty())
				validate = true;
		return validate;
	}
	
	
	public void cleanInputs(){
		textFrame.setText("");
		textPayload.setText("");
		textAdress.setText("");
		textAdressS.setText("");
		textControl.setText("");
		cadFrame = "";
		btnGenerate.setEnabled(true);
		options.setSelection(0);
	}
	

	public String stuffed(String payload){
		while(payload.length()<92)
			payload += "0";
		return payload;
		
	}
	
	//Recuperación de texto
	public static ArrayList<Integer> text2decimal(String text){
		ArrayList<Integer> numbers = new ArrayList<Integer>();
		for (int i=0; i<text.length();i++){
			numbers.add((int) text.charAt(i) );
		}

	    return numbers;
		
	}
	
	//Conversiones
	public String text2Hexa(String text){
		ArrayList<Integer> numbers = text2decimal(text);
		String cadHexa = ""; 

		for(int number: numbers){
			String str = decimal2hexa(number);
			StringBuilder strb = new StringBuilder(str);
			str = strb.reverse().toString();
			cadHexa += str;
		}
		
		return cadHexa;
	}
	
	
	public  String decimal2hexa(int decimal){
		int rest;
		String hexa ="";
		while(decimal>0){
			rest = decimal % 16;
			char charhex = charHexa[rest];
			hexa += charhex;
			decimal = decimal/16;
		}	
		return hexa;
	}
	
	
	
	public String generateRandom(int tam){
		String cad = "";
		for(int i=0; i<tam; i++){
			Random rand = new Random();	
			int number = rand.nextInt(tam);
			cad += charHexa[number];
			
		}
		return cad;	
	}
	
	
	//Add cadenas
	public void addFlag(String flag){
		cadFrame += " " + flag;
	}
	
	public void addAdress(String adress){
		cadFrame += " " + adress;
	}
	
	public void addSOF(String sof){
		cadFrame += " " + sof;
	}
	
	public void addControl(String control){
		cadFrame += " "+ control; 
	}
	
	public void error(){
		Toast.makeText(getApplicationContext(),"Ingresa los campos correctamente", Toast.LENGTH_LONG).show();
		btnGenerate.setEnabled(true);
	}
	
	public void initComponents(){

		textTitle = (TextView) findViewById(R.id.textTitle);
	    textAdress = (EditText) findViewById(R.id.adress);
	    textAdressS = (EditText) findViewById(R.id.adressSource); 
	    textControl = (EditText) findViewById(R.id.control);
	    
	    SpinnerData();
	    options = (Spinner) findViewById(R.id.spinner);
	    String[] lista = getResources().getStringArray(R.array.protocol_array);
	    ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, lista);
	    options.setAdapter(adaptador);
	    
	    textPayload = (EditText) findViewById(R.id.payload);
	    textFrame = (TextView)findViewById(R.id.frame);
	    textFrame.setMovementMethod( new ScrollingMovementMethod());
	    btnGenerate = (Button) findViewById(R.id.generateFrame);
	    btnGenerate.setOnClickListener(this);
	    btnClean = (Button) findViewById(R.id.clean);
	    btnClean.setOnClickListener(this);
	}
	
	public void SpinnerData(){
		map.put("Reservado", "001F");
		map.put("Internet Protocol", "0021");
		map.put("OSI Network Layer", "0023");
		map.put("Xerox NS IDP", "0025");
		map.put("DECnet Phase IV", "0027");
		map.put("Appletalk", "0029");
		map.put("Novell IPX", "002B");
		map.put("Luxcom", "0231");
		map.put("Sigma Network System", "0233");
		map.put("Internet Protocol Control Protocol", "8021");
		map.put("OSI Network Layer Control Protocol", "8023");
		map.put("Xerox NS IDP Control Protocol", "8025");
		map.put("DECnet Phase IV Control Protocol", "8027");
		map.put("Appletalk Control Protocol", "8029");
		map.put("Novell IPX Control Protocol", "802B");
		map.put("Link Control Protocol", "C021");
		map.put("PAP", "C023");
		map.put("CHAP", "C223");
	}
}
