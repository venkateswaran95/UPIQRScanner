package com.example.qrscanner;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QRScanner extends Activity implements View.OnClickListener{
    private Button buttonScan,proceed;
    private TextView textViewName, textViewAddress;
    private IntentIntegrator qrScan;
    private Boolean initialScan=true;
    public int order=0;
    private String scannedData,regex = "^([0-9]){10,15}[a-zA-Z0-9]{10,15}";
    Pattern pattern;
    private PayWith Pay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_qr_scanner);
        buttonScan = (Button) findViewById(R.id.buttonScan);
        proceed=(Button) findViewById(R.id.proceed);
        proceed.setVisibility(View.INVISIBLE);
        textViewName = (TextView) findViewById(R.id.textViewName);
    //    textViewAddress = (TextView) findViewById(R.id.textViewAddress);
        buttonScan.setOnClickListener(this);
        pattern = Pattern.compile(regex);
        qrScan=new IntentIntegrator(this);
        Log.v("ON Create","is called Order"+order);
        order++;
        proceed.setOnClickListener(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v("ON Pause","is called"+order);
        order++;
        Log.v("isFinishing",isFinishing()+"");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v("ON Start","is called"+order);
        order++;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v("ON Destroy","is called"+order);
        order++;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("ON Destroy","is called"+order);
        order++;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v("ON Destroy","is called"+order);
        order++;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    scannedData=result.getContents();
                    if(scannedData.substring(0,6).equals("upi://")) {
                      //  String formattedText = "<b>UPI: </b> <i>"+Pay.getUPIAddress()+"</i><br/ ><b>Name:</b>"+Pay.getPayeeName();

                        Pay = new PayWith(result.getContents());
                        Log.v("UPI detection ", Pay.parseURI());
                       // textViewName.setText(Html.fromHtml(formattedText));
                         textViewName.setText("UPI Address: "+Pay.getUPIAddress()+"\n "+"Name: "+ Pay.getPayeeName());
                        proceed.setVisibility(View.VISIBLE);

                    }
                    else if(scannedData.substring(0,3).equals("htt")) {
                        textViewName.setText(scannedData);
                        proceed.setVisibility(View.VISIBLE);
                    }
                    else {
                        Matcher matcher=pattern.matcher(scannedData);
                        if(matcher.matches())
                            textViewName.setText("Paytm "+scannedData);
                        else
                            textViewName.setText(scannedData);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onClick(View view){
        if(view==QRScanner.this.buttonScan) {
            qrScan.setOrientationLocked(false);
            qrScan.initiateScan();
        }
        else{
            if(scannedData.substring(0,3).equals("upi")) {
                    String UPI = getUPIString(Pay.getUPIAddress(), Pay.getPayeeName());
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(UPI));
                    Intent chooser = Intent.createChooser(intent, "Pay with...");
                    startActivity(chooser);
                }
                else{
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(scannedData));
                    Intent chooser = Intent.createChooser(intent, "Browse with...");
                    startActivity(chooser);

                }
            }
    }


    private String getUPIString(String payeeAddress, String payeeName) {
        /*String UPI = "upi://pay?pa=" + payeeAddress + "&pn=" + payeeName
                + "&mc=" + payeeMCC + "&tid=" + trxnID + "&tr=" + trxnRefId
                + "&tn=" + trxnNote + "&am=" + payeeAmount + "&cu=" + currencyCode
                + "&refUrl=" + refUrl;*/
        String UPI = "upi://pay?pa=" + payeeAddress + "&pn=" + payeeName;
        return UPI.replace(" ", "+");
    }

}