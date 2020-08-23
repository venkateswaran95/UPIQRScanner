package com.example.qrscanner;

import android.net.Uri;

public class PayWith {
    String PayURI;
    Uri uri;

    PayWith(String URI){
        PayURI=URI;

    }
     String parseURI(){
        try {
            uri = Uri.parse(PayURI);
            }
        catch (Exception e){
            return "URI parsing error";
        }
        return uri.getScheme();

    }
    String getUPIAddress(){
        return uri.getQueryParameter("pa");
    }
    String getPayeeName(){
        return uri.getQueryParameter("pn");
    }



}
